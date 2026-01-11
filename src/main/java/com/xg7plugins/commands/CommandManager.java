package com.xg7plugins.commands;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.setup.PluginSetup;
import com.xg7plugins.commands.executors.MainCommand;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;

import com.xg7plugins.commands.executors.PluginCommandExecutor;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionMethod;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages command registration, execution, and tab completion for the plugin.
 * Handles the main plugin command and its subcommands, including permission checks,
 * command configurations, and async execution capabilities.
 */
@Getter
public class CommandManager {

    private final Plugin plugin;
    private final Map<String, CommandNode> commandNodeMap = new HashMap<>();
    private final List<Command> commandList = new ArrayList<>();
    private final PluginCommandExecutor executor;
    private final AntiTab antiTab;

    public CommandManager(Plugin plugin) {
        this.plugin = plugin;
        this.executor = new PluginCommandExecutor(this);
        this.antiTab = new AntiTab(this);
    }

    /**
     * Registers plugin commands and their aliases in the server's command map.
     * Handles configuration-based command enabling/disabling and sets up command
     * executors, tab completer, and aliases for both main and sub commands.
     *
     * @param commands List of commands to register
     */
    public void registerCommands(List<Command> commands) {

        CommandMap commandMap = ReflectionObject.of(Bukkit.getServer()).getField("commandMap");
        PluginSetup plConfig = plugin.getClass().getAnnotation(PluginSetup.class);

        PluginCommand mainCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                .newInstance(plConfig.mainCommandName(), plugin.getJavaPlugin())
                .getObject();

        mainCommand.setExecutor(executor);
        mainCommand.setTabCompleter(executor);
        mainCommand.setAliases(Arrays.asList(plConfig.mainCommandAliases()));
        commandMap.register(plConfig.mainCommandName(), mainCommand);

        MainCommand mainPluginCommand = new MainCommand(plugin);
        CommandNode mainCommandNode = new CommandNode(mainPluginCommand, plConfig.mainCommandName());

        ReflectionMethod method = ReflectionObject.of(mainPluginCommand).getMethod("onCommand", CommandSender.class, CommandArgs.class);

        mainCommandNode.setCommandMethod(method);

        this.commandNodeMap.put(plConfig.mainCommandName(), mainCommandNode);
        for (String alias : plConfig.mainCommandAliases()) this.commandNodeMap.put(alias, mainCommandNode);

        for (Command command : commands) {
            if (command == null) continue;

            if (!command.getClass().isAnnotationPresent(CommandSetup.class)) {
                plugin.getDebug().severe("Commands must be annotated with @CommandSetup interface!!");
                continue;
            }

            CommandSetup setup = command.getCommandSetup();

            if (!setup.isEnabled().configName().isEmpty()) {
                ConfigFile config = ConfigFile.of(setup.isEnabled().configName(), plugin);
                boolean invert = setup.isEnabled().invert();
                boolean enabled = config != null && config.root().get(setup.isEnabled().path(), false);
                if (invert == enabled) {
                    plugin.getDebug().info("load", "Command " + setup.name() + " is disabled by configuration");
                    continue;
                }
            }

            ConfigSection cfg = ConfigFile.of("commands", plugin).root();

            if (!cfg.contains(setup.name())) {
                plugin.getDebug().warn("load", "Command " + setup.name() + " not found in commands.yml - skipping registration");
                continue;
            }

            List<String> aliases = cfg.getList(setup.name(), String.class).orElse(Collections.emptyList());

            String fullName = plConfig.mainCommandName() + setup.name();
            PluginCommand pluginCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                    .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                    .newInstance(fullName, plugin.getJavaPlugin())
                    .getObject();

            pluginCommand.setAliases(aliases);
            pluginCommand.setExecutor(executor);
            pluginCommand.setDescription(setup.description());
            pluginCommand.setUsage(setup.syntax());
            pluginCommand.setTabCompleter(executor);
            commandMap.register(plConfig.mainCommandName(), pluginCommand);

            plugin.getDebug().info("load", "Registered command: " + fullName + " with aliases: " + aliases);
            registerCommandNodes(command);
            commandList.add(command);
        }

        plugin.getDebug().info("load", "Successfully loaded " + commands.size() + " Commands!");
    }


    private void registerCommandNodes(Command command) {
        CommandSetup setup = command.getCommandSetup();

        String rootName = command.getPlugin().getPluginSetup().mainCommandName() + setup.name();
        CommandNode root = new CommandNode(command, setup.name());
        ConfigSection cfg = ConfigFile.of("commands", plugin).root();


        List<String> aliases = cfg.getList(setup.name(), String.class).orElse(Collections.emptyList());

        AtomicInteger depthCounter = new AtomicInteger(0);

        ReflectionObject.of(command).getMethods().stream()
                .filter(m -> m.hasAnnotation(CommandConfig.class))
                .sorted((a, b) -> {
                    CommandConfig execA = a.getAnnotation(CommandConfig.class);
                    CommandConfig execB = b.getAnnotation(CommandConfig.class);

                    if (execA.name().equals("root")) return -1;
                    if (execB.name().equals("root")) return 1;

                    return Integer.compare(execA.depth(), execB.depth());
                })
                .forEach(method -> {
                    CommandConfig exec = method.getAnnotation(CommandConfig.class);
                    String nodeName = exec.name();

                    System.out.println("Trying to register " + exec.name() + " with parent " + exec.parent());
                    System.out.println("Method: " + method.getMethod().getName() + "()");
                    System.out.println("LOOP " + depthCounter.incrementAndGet());

                    if (nodeName.equals("root")) {
                        root.setCommandMethod(method);
                        return;
                    }

                    CommandNode child = new CommandNode(command, nodeName);
                    child.setCommandMethod(method);

                    if (!exec.parent().isEmpty()) {
                        Pair<String, CommandNode> parentPair = findNodePathByNameAndDepth(root,
                                exec.parent(), exec.depth() - 1, 1, "");

                        if (parentPair == null) return;

                        CommandNode parent = parentPair.getSecond();

                        parent.addChild(child);

                        String fullPath = parentPair.getFirst() + "_" + nodeName;

                        if (!cfg.contains(fullPath)) {
                            plugin.getDebug().warn("load", "Subcommand " + nodeName + " of " + setup.name() + " not found in commands.yml - skipping registration");
                            return;
                        }

                        List<String> childAliases = cfg.getList(fullPath, String.class).orElse(Collections.emptyList());
                        childAliases.forEach(alias -> parent.mapChild(alias, child));

                    } else {

                        if (!cfg.contains(setup.name() + "_" + nodeName)) {
                            plugin.getDebug().warn("load", "Subcommand " + nodeName + " of " + setup.name() + " not found in commands.yml - skipping registration");
                            return;
                        }
                        System.out.println("Adding child: " + child.getName() + " to root: " + root.getName());
                        root.addChild(child);
                        List<String> childAliases = cfg
                                .getList(setup.name() + "_" + nodeName, String.class)
                                .orElse(Collections.emptyList());

                        childAliases.forEach(alias -> root.mapChild(alias, child));
                    }
                });

        commandNodeMap.putIfAbsent(rootName, root);
        for (String alias : aliases)
            commandNodeMap.putIfAbsent(alias, root);

        System.out.println("Node Map: " + commandNodeMap);
    }


    private Pair<String, CommandNode> findNodePathByNameAndDepth(
            CommandNode current, String name, int depth, int currentDepth, String path) {

        System.out.println("[DEBUG] enter findNodePathByNameAndDepth: name=" + name
                + ", depth=" + depth + ", currentDepth=" + currentDepth
                + ", path=" + path + ", current=" + (current != null ? current.getName() : "null"));

        String currentPath = path.isEmpty() ? current.getName() : path + "_" + current.getName();
        System.out.println("[DEBUG] currentPath = " + currentPath);

        if (current.getName().equalsIgnoreCase(name) && currentDepth == depth) {
            System.out.println("[DEBUG] matched current node at depth: " + currentPath);
            return Pair.of(currentPath, current);
        }

        for (CommandNode child : current.getChildren()) {
            System.out.println("[DEBUG] inspecting child: " + child.getName() + " of parent: " + current.getName());

            if (child.getName().equalsIgnoreCase(name)) {
                String foundPath = currentPath + "_" + child.getName();
                System.out.println("[DEBUG] matched direct child: " + foundPath);
                return Pair.of(foundPath, child);
            }

            System.out.println("[DEBUG] recursing into child: " + child.getName() + " (nextDepth=" + (currentDepth + 1) + ")");
            Pair<String, CommandNode> found = findNodePathByNameAndDepth(child, name, depth, currentDepth + 1, currentPath);
            if (found != null) {
                System.out.println("[DEBUG] found in recursion: " + found.getFirst());
                return found;
            }
        }

        System.out.println("[DEBUG] not found under: " + current.getName() + " for name=" + name + " depth=" + depth);
        return null;
    }



    /**
     * Retrieves a registered command by its name.
     * @param name The name of the command to retrieve
     * @return The Command instance if found, null otherwise
     */
    public Command getCommand(String name) {
        return commandList.stream().filter(c -> c.getCommandSetup().name().equals(name)).findFirst().orElse(null);
    }

    /**
     * Retrieves the root CommandNode for a given command name.
     * @param name The name of the command
     * @return The root CommandNode associated with the command name, or null if not found
     */
    public CommandNode getRootCommandNode(String name) {
        return commandNodeMap.get(name);
    }
}
