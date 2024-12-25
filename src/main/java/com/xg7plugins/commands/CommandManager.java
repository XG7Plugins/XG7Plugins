package com.xg7plugins.commands;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.PluginConfigurations;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionMethod;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.*;

@AllArgsConstructor
public class CommandManager implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    @Getter
    private final HashMap<String, ICommand> commands = new HashMap<>();

    public void registerCommands(ICommand... commands) {

        plugin.getLog().loading("Loading Commands...");

        CommandMap commandMap = ReflectionObject.of(Bukkit.getServer()).getField("commandMap");

        PluginConfigurations plConfig = plugin.getClass().getAnnotation(PluginConfigurations.class);;

        PluginCommand mainCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                .newInstance(plConfig.mainCommandName(), plugin)
                .getObject();

        mainCommand.setExecutor(this);
        mainCommand.setTabCompleter(this);
        mainCommand.setAliases(Arrays.asList(plConfig.mainCommandAliases()));
        commandMap.register(plConfig.mainCommandName(), mainCommand);

        this.commands.put(plConfig.mainCommandName(), new MainCommand(plugin));

        Arrays.stream(commands).forEach(command -> {

            if (command == null) return;

            if (!command.isEnabled()) return;

            if (!command.getClass().isAnnotationPresent(Command.class)) {
                plugin.getLog().severe("Commands must be annotated with @Command interface!!");
                return;
            }

            Command commandSetup = command.getClass().getAnnotation(Command.class);

            String aliases = plugin.getConfigsManager().getConfig("commands").get(commandSetup.name(), String.class).orElse(null);
            if (aliases == null) return;

            PluginCommand pluginCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                    .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                    .newInstance(plConfig.mainCommandName() + commandSetup.name(), plugin)
                    .getObject();

            if (!aliases.isEmpty()) {
                List<String> aliasesList = Arrays.asList(aliases.split(", "));
                aliasesList.add(commandSetup.name());

                List<String> newAliases = new ArrayList<>();
                for (String alias : aliasesList) {
                    for (String plAlias : pluginCommand.getAliases()) {
                        newAliases.add(plAlias + alias);
                    }
                }

                pluginCommand.setAliases(newAliases);
            }


            pluginCommand.setExecutor(this);
            pluginCommand.setDescription(commandSetup.description());
            pluginCommand.setUsage(commandSetup.syntax());
            pluginCommand.setTabCompleter(this);
            commandMap.register(plConfig.mainCommandName() + commandSetup.name(), pluginCommand);

            this.commands.put(commandSetup.name(), command);

        });

        plugin.getLog().loading("Successfully loaded Commands!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] strings) {

        PluginConfigurations plConfig = plugin.getClass().getAnnotation(PluginConfigurations.class);;

        ICommand command = commands.get(cmd.getName());

        if (command instanceof MainCommand) {
            if (strings.length == 0) {
                Text.format("lang:[commands.syntax-error]",XG7Plugins.getInstance())
                        .replace("[SYNTAX]", cmd.getUsage())
                        .send(commandSender);
                return true;
            }

            if (strings.length > 1) {
                if (strings[0].equalsIgnoreCase("help")) {
                    Text.format("lang:[commands.help]",XG7Plugins.getInstance())
                            .replace("[COMMANDS]", String.join(", ", commands.keySet()))
                            .send(commandSender);
                    return true;
                }

                command = commands.get(plConfig + strings[0]);

                strings = Arrays.copyOfRange(strings, 1, strings.length - 1);
            }

        }


        if (processSubCommands(command, commandSender, strings, 0)) return true;

        Command commandConfig = command.getClass().getAnnotation(Command.class);

        if (!commandSender.hasPermission(commandConfig.permission()) && !commandConfig.permission().isEmpty()) {
            Text.format("lang:[commands.no-permission]",XG7Plugins.getInstance()).send(commandSender);
            return true;
        }

        if (commandConfig.isPlayerOnly() && !(commandSender instanceof Player)) {
            Text.format("lang:[commands.not-a-player]",XG7Plugins.getInstance()).send(commandSender);
            return true;
        }
        if (commandConfig.isConsoleOnly() && commandSender instanceof Player) {
            Text.format("lang:[commands.is-a-player]",XG7Plugins.getInstance()).send(commandSender);
            return true;
        }
        if (commandSender instanceof Player) {
            if (!commandConfig.isInEnabledWorldOnly() && plugin.getEnabledWorlds().contains(((Player) commandSender).getWorld().getName()) && !plugin.getEnabledWorlds().isEmpty()) {
                Text.format("lang:[commands.disabled-world]",XG7Plugins.getInstance()).send(commandSender);
                return true;
            }
        }

        CommandArgs commandArgs = new CommandArgs(strings);

        if (commandConfig.isAsync()) {

            final ICommand finalCommand = command;

            XG7Plugins.taskManager().runAsyncTask("commands", () -> finalCommand.onCommand(commandSender,commandArgs));

            return true;
        }

        command.onCommand(commandSender,commandArgs);

        return true;
    }

    public boolean processSubCommands(ICommand command, CommandSender sender, String[] args, int index) {

        if (args.length == index) return false;

        ICommand[] subCommands = command.getSubCommands();

        if (subCommands.length == 0) return false;

        ICommand subCommandChosen = null;

        for (ICommand subCommand : subCommands) {
            Command configs = subCommand.getClass().getAnnotation(Command.class);

            if (configs.name().equalsIgnoreCase(args[index]) || Arrays.asList(configs.aliases()).contains(args[index])) {
                subCommandChosen = subCommand;
                break;
            }
        }

        if (subCommandChosen == null) return false;

        if (subCommandChosen.getSubCommands().length > 0 && processSubCommands(subCommandChosen, sender, args, index + 1)) return true;

        Command commandConfig = subCommandChosen.getClass().getAnnotation(Command.class);

        if (!sender.hasPermission(commandConfig.permission()) && !commandConfig.permission().isEmpty()) {
            Text.format("lang:[commands.no-permission]",XG7Plugins.getInstance()).send(sender);
            return true;
        }

        if (commandConfig.isPlayerOnly() && !(sender instanceof Player)) {
            Text.format("lang:[commands.not-a-player]",XG7Plugins.getInstance()).send(sender);
            return true;
        }
        if (commandConfig.isConsoleOnly() && sender instanceof Player) {
            Text.format("lang:[commands.is-a-player]",XG7Plugins.getInstance()).send(sender);
            return true;
        }
        if (sender instanceof Player) {
            if (!commandConfig.isInEnabledWorldOnly() && plugin.getEnabledWorlds().contains(((Player) sender).getWorld().getName()) && !plugin.getEnabledWorlds().isEmpty()) {
                Text.format("lang:[commands.disabled-world]",XG7Plugins.getInstance()).send(sender);
                return true;
            }
        }

        CommandArgs commandArgs = new CommandArgs(Arrays.copyOfRange(args, index, args.length - 1));

        if (commandConfig.isAsync()) {

            final ICommand finalSubCommand = subCommandChosen;

            XG7Plugins.taskManager().runAsyncTask("commands", () -> finalSubCommand.onCommand(sender,commandArgs));

            return true;
        }

        subCommandChosen.onCommand(sender,commandArgs);

        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] strings) {
        return commands.get(cmd.getName()).onTabComplete(commandSender,new CommandArgs(strings));
    }
}
