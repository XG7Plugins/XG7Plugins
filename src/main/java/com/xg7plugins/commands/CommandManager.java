package com.xg7plugins.commands;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.PluginSetup;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.*;

@AllArgsConstructor
public class CommandManager implements CommandExecutor, TabCompleter, Manager {

    private final Plugin plugin;
    @Getter
    private final HashMap<String, Command> commands = new HashMap<>();

    public void registerCommands(List<Command> commands) {

        CommandMap commandMap = ReflectionObject.of(Bukkit.getServer()).getField("commandMap");

        PluginSetup plConfig = plugin.getClass().getAnnotation(PluginSetup.class);;

        PluginCommand mainCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                .newInstance(plConfig.mainCommandName(), plugin)
                .getObject();

        mainCommand.setExecutor(this);
        mainCommand.setTabCompleter(this);
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

            Config config = Config.of(commandSetup.isEnabled().configName(), plugin);

            boolean invert = commandSetup.isEnabled().invert();
            if (config != null && config.get(commandSetup.isEnabled().path(), Boolean.class).orElse(false) == invert) return;

            List<String> aliases = Config.of("commands", plugin).getList(commandSetup.name(), String.class).orElse(null);
            if (aliases == null) return;

            PluginCommand pluginCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                    .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                    .newInstance(plConfig.mainCommandName() + commandSetup.name(), plugin)
                    .getObject();

            if (!aliases.isEmpty()) {
                aliases.add(commandSetup.name());

                List<String> newAliases = new ArrayList<>();
                for (String alias : aliases) {
                    for (String plAlias : plConfig.mainCommandAliases()) newAliases.add(plAlias + alias);
                    newAliases.add(alias);
                }
                pluginCommand.setAliases(newAliases);
            }

            pluginCommand.setExecutor(this);
            pluginCommand.setDescription(commandSetup.description());
            pluginCommand.setUsage(commandSetup.syntax());
            pluginCommand.setTabCompleter(this);
            commandMap.register(plConfig.mainCommandName() + commandSetup.name(), pluginCommand);

            this.commands.put(plConfig.mainCommandName() + commandSetup.name(), command);

        });

        plugin.getDebug().loading("Successfully loaded Commands!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] strings) {

        PluginSetup plConfig = plugin.getClass().getAnnotation(PluginSetup.class);

        Command command = commands.get(cmd.getName());

        CommandSetup commandConfig = command.getCommandConfigurations();


        if (command instanceof MainCommand) {
            if (!sender.hasPermission(commandConfig.permission())) {
                CommandMessages.NO_PERMISSION.send(sender);
                return true;
            }
            if (strings.length == 0) {
                CommandMessages.SYNTAX_ERROR.send(sender, commandConfig.syntax());
                return true;
            }

            if (strings[0].equalsIgnoreCase("help")) {
                command.onCommand(sender,new CommandArgs(strings));
                return true;
            }
            command = commands.get(plConfig.mainCommandName() + strings[0]);
            if (command == null) {
                CommandMessages.COMMAND_NOT_FOUND.send(sender);
                return true;
            }
            strings = Arrays.copyOfRange(strings, 1, strings.length);
        }

        processCommand(command, sender, strings);

        return true;
    }

    public boolean processSubCommands(Command command, CommandSender sender, String[] args, int index) {
        if (command == null) return false;
        if (args.length == index) return false;

        List<Command> subCommands = command.getSubCommands();

        if (subCommands.isEmpty()) return false;

        Command subCommandChosen = null;

        for (Command subCommand : subCommands) {
            if (subCommand.getCommandConfigurations().name().equalsIgnoreCase(args[index])) {
                subCommandChosen = subCommand;
                break;
            }
        }

        if (subCommandChosen == null) return false;

        if (!subCommandChosen.getSubCommands().isEmpty() && processSubCommands(subCommandChosen, sender, args, index + 1)) return true;

        processCommand(subCommandChosen, sender, Arrays.copyOfRange(args, index + 1, args.length));

        return true;
    }
    
    private void processCommand(Command command, CommandSender sender, String[] strings) {
        if (processSubCommands(command, sender, strings, 0)) return;
        
        CommandSetup commandConfig = command.getCommandConfigurations();

        if (!sender.hasPermission(commandConfig.permission()) && !commandConfig.permission().isEmpty()) {
            CommandMessages.NO_PERMISSION.send(sender);
            return;
        }
        if (commandConfig.isPlayerOnly() && !(sender instanceof Player)) {
            CommandMessages.NOT_A_PLAYER.send(sender);
            return;
        }
        if (commandConfig.isConsoleOnly() && sender instanceof Player) {
            CommandMessages.IS_A_PLAYER.send(sender);
            return;
        }
        if (sender instanceof Player) {
            if (commandConfig.isInEnabledWorldOnly() && !XG7PluginsAPI.isInWorldEnabled(plugin, ((Player) sender))) {
                CommandMessages.DISABLED_WORLD.send(sender);
                return;
            }
        }

        CommandArgs commandArgs = new CommandArgs(strings);

        if (commandConfig.isAsync()) {

            final Command finalCommand = command;

            XG7PluginsAPI.taskManager().runAsyncTask(plugin,"commands", () -> finalCommand.onCommand(sender,commandArgs));

            return;
        }

        try {
            command.onCommand(sender,commandArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] strings) {

        PluginSetup plConfig = plugin.getClass().getAnnotation(PluginSetup.class);;

        Command command = commands.get(cmd.getName());

        CommandSetup commandConfig = command.getCommandConfigurations();

        if (command instanceof MainCommand) {
            if (strings.length == 0) return Collections.emptyList();

            if (sender.hasPermission(commandConfig.permission()) && !sender.hasPermission("xg7plugins.command.anti-tab-bypass")) return Collections.emptyList();

            if (strings.length > 1) {

                if (strings[0].equalsIgnoreCase("help")) return command.onTabComplete(sender,new CommandArgs(strings));

                command = commands.get(plConfig.mainCommandName() + strings[0]);

                if (command == null) return Collections.emptyList();

                strings = Arrays.copyOfRange(strings, 1, strings.length);

            }

        }

        if (sender.hasPermission(commandConfig.permission()) && !sender.hasPermission("xg7plugins.command.anti-tab-bypass")) {
            return Collections.emptyList();
        }

        return command.onTabComplete(sender,new CommandArgs(strings));
    }
}
