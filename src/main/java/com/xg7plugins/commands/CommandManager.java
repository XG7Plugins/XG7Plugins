package com.xg7plugins.commands;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.PluginSetup;
import com.xg7plugins.commands.executors.MainCommand;
import com.xg7plugins.commands.executors.PluginCommandExecutor;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.*;


import java.util.*;

/**
 * Manages command registration, execution, and tab completion for the plugin.
 * Handles the main plugin command and its subcommands, including permission checks,
 * command configurations, and async execution capabilities.
 */
@Getter
public class CommandManager implements Manager {

    private final Plugin plugin;
    private final Map<String, Command> commands = new HashMap<>();
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

        // Registrar comando principal
        PluginCommand mainCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                .newInstance(plConfig.mainCommandName(), plugin)
                .getObject();

        mainCommand.setExecutor(executor);
        mainCommand.setTabCompleter(executor);
        mainCommand.setAliases(Arrays.asList(plConfig.mainCommandAliases()));
        commandMap.register(plConfig.mainCommandName(), mainCommand);

        this.commands.put(plConfig.mainCommandName(), new MainCommand(plugin));

        commands.forEach(command -> {
            if (command == null) return;

            if (!command.getClass().isAnnotationPresent(CommandSetup.class)) {
                plugin.getDebug().severe("Commands must be annotated with @CommandSetup interface!!");
                return;
            }

            CommandSetup commandSetup = command.getCommandConfigurations();

            if (!commandSetup.isEnabled().configName().isEmpty()) {
                Config config = Config.of(commandSetup.isEnabled().configName(), plugin);

                boolean invert = commandSetup.isEnabled().invert();
                boolean enabled = config != null && config.get(commandSetup.isEnabled().path(), Boolean.class).orElse(false);

                if (invert == enabled) {
                    plugin.getDebug().info("Command " + commandSetup.name() + " is disabled by configuration");
                    return;
                }
            }

            List<String> configAliases = Config.of("commands", plugin)
                    .getList(commandSetup.name(), String.class)
                    .orElse(new ArrayList<>());

            Config commandConfig = Config.of("commands", plugin);
            if (!commandConfig.contains(commandSetup.name())) {
                plugin.getDebug().warn("Command " + commandSetup.name() + " not found in commands.yml - skipping registration");
                return;
            }

            String fullCommandName = plConfig.mainCommandName() + commandSetup.name();

            PluginCommand pluginCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                    .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                    .newInstance(fullCommandName, plugin)
                    .getObject();

            List<String> allAliases = new ArrayList<>(configAliases);

            for (String mainAlias : plConfig.mainCommandAliases())
                allAliases.add(mainAlias + commandSetup.name());

            pluginCommand.setAliases(allAliases);
            pluginCommand.setExecutor(executor);
            pluginCommand.setDescription(commandSetup.description());
            pluginCommand.setUsage(commandSetup.syntax());
            pluginCommand.setTabCompleter(executor);

            this.commands.putIfAbsent(fullCommandName, command);

            commandMap.register(fullCommandName, pluginCommand);

            for (String alias : allAliases) {
                if (this.commands.containsKey(alias)) continue;
                this.commands.put(alias, command);
            }

            this.commandList.add(command);

            plugin.getDebug().info("Registered command: " + fullCommandName + " with aliases: " + allAliases);
        });

        plugin.getDebug().loading("Successfully loaded " + commands.size() + " Commands!");
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }
}
