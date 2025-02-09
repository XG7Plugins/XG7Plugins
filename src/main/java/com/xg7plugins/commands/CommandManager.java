package com.xg7plugins.commands;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.PluginConfigurations;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
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
                plugin.getDebug().error("Commands must be annotated with @Command interface!!");
                return;
            }

            Command commandSetup = command.getClass().getAnnotation(Command.class);

            List<String> aliases = plugin.getConfigsManager().getConfig("commands").getList(commandSetup.name(), String.class).orElse(null);
            if (aliases == null) return;

            PluginCommand pluginCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                    .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                    .newInstance(plConfig.mainCommandName() + commandSetup.name(), plugin)
                    .getObject();

            if (!aliases.isEmpty()) {
                aliases.add(commandSetup.name());

                List<String> newAliases = new ArrayList<>();
                for (String alias : aliases) {
                    for (String plAlias : plConfig.mainCommandAliases()) {
                        newAliases.add(plAlias + alias);
                    }
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
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] strings) {

        PluginConfigurations plConfig = plugin.getClass().getAnnotation(PluginConfigurations.class);;

        ICommand command = commands.get(cmd.getName());

        Command commandConfig = command.getClass().getAnnotation(Command.class);


        if (command instanceof MainCommand) {
            if (!commandSender.hasPermission(commandConfig.permission())) {
                Text.fromLang(commandSender, XG7Plugins.getInstance(),"commands.no-permission").thenAccept(text -> text.send(commandSender));
                return true;
            }
            if (strings.length == 0) {
                ICommand finalCommand1 = command;
                Text.fromLang(commandSender, XG7Plugins.getInstance(), "commands.syntax-error").thenAccept(text -> {
                        text.replace("[SYNTAX]", finalCommand1.getClass().getAnnotation(Command.class).syntax())
                            .send(commandSender);
                });
                return true;
            }

            if (strings[0].equalsIgnoreCase("help")) {
                command.onCommand(commandSender,new CommandArgs(strings));
                return true;
            }

            command = commands.get(plConfig.mainCommandName() + strings[0]);

            if (command == null) {
                Text.fromLang(commandSender, XG7Plugins.getInstance(), "commands.command-not-found")
                        .thenAccept(text -> text.send(commandSender));
                return true;
            }

            strings = Arrays.copyOfRange(strings, 1, strings.length);

            commandConfig = command.getClass().getAnnotation(Command.class);

        }


        if (processSubCommands(command, commandSender, strings, 0)) return true;

        if (!commandSender.hasPermission(commandConfig.permission()) && !commandConfig.permission().isEmpty()) {
            Text.fromLang(commandSender, XG7Plugins.getInstance(), "commands.no-permission").thenAccept(text -> text.send(commandSender));
            return true;
        }

        if (commandConfig.isPlayerOnly() && !(commandSender instanceof Player)) {
            Text.fromLang(commandSender, XG7Plugins.getInstance(), "commands.not-a-player").thenAccept(text -> text.send(commandSender));
            return true;
        }
        if (commandConfig.isConsoleOnly() && commandSender instanceof Player) {
            Text.fromLang(commandSender, XG7Plugins.getInstance(), "commands.is-a-player").thenAccept(text -> text.send(commandSender));
            return true;
        }
        if (commandSender instanceof Player) {
            if (commandConfig.isInEnabledWorldOnly() && !plugin.getEnabledWorlds().contains(((Player) commandSender).getWorld().getName())) {
                Text.fromLang(commandSender, XG7Plugins.getInstance(), "commands.disabled-world").thenAccept(text -> text.send(commandSender));
                return true;
            }
        }

        CommandArgs commandArgs = new CommandArgs(strings);

        if (commandConfig.isAsync()) {

            final ICommand finalCommand = command;

            XG7Plugins.taskManager().runAsyncTask(plugin,"commands", () -> finalCommand.onCommand(commandSender,commandArgs));

            return true;
        }

        try {
            command.onCommand(commandSender,commandArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean processSubCommands(ICommand command, CommandSender sender, String[] args, int index) {
        if (command == null) return false;
        if (args.length == index) return false;

        ICommand[] subCommands = command.getSubCommands();

        if (subCommands.length == 0) return false;

        ICommand subCommandChosen = null;

        for (ICommand subCommand : subCommands) {
            Command configs = subCommand.getClass().getAnnotation(Command.class);

            if (configs.name().equalsIgnoreCase(args[index])) {
                subCommandChosen = subCommand;
                break;
            }
        }

        if (subCommandChosen == null) return false;

        if (subCommandChosen.getSubCommands().length > 0 && processSubCommands(subCommandChosen, sender, args, index + 1)) return true;

        Command commandConfig = subCommandChosen.getClass().getAnnotation(Command.class);

        if (!sender.hasPermission(commandConfig.permission()) && !commandConfig.permission().isEmpty()) {
            Text.fromLang(sender, XG7Plugins.getInstance(), "commands.no-permission").thenAccept(text -> text.send(sender));
            return true;
        }

        if (commandConfig.isPlayerOnly() && !(sender instanceof Player)) {
            Text.fromLang(sender, XG7Plugins.getInstance(), "commands.not-a-player").thenAccept(text -> text.send(sender));
            return true;
        }
        if (commandConfig.isConsoleOnly() && sender instanceof Player) {
            Text.fromLang(sender, XG7Plugins.getInstance(), "commands.is-a-player").thenAccept(text -> text.send(sender));
            return true;
        }
        if (sender instanceof Player) {
            if (commandConfig.isInEnabledWorldOnly() && !plugin.getEnabledWorlds().contains(((Player) sender).getWorld().getName())) {
                Text.fromLang(sender, XG7Plugins.getInstance(), "commands.disabled-world").thenAccept(text -> text.send(sender));
                return true;
            }
        }

        CommandArgs commandArgs = new CommandArgs(Arrays.copyOfRange(args, index + 1, args.length));

        if (commandConfig.isAsync()) {

            final ICommand finalSubCommand = subCommandChosen;

            XG7Plugins.taskManager().runAsyncTask(plugin,"commands", () -> finalSubCommand.onCommand(sender,commandArgs));

            return true;
        }

        try {
            subCommandChosen.onCommand(sender,commandArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] strings) {

        PluginConfigurations plConfig = plugin.getClass().getAnnotation(PluginConfigurations.class);;

        ICommand command = commands.get(cmd.getName());

        Command commandConfig = command.getClass().getAnnotation(Command.class);

        if (command instanceof MainCommand) {
            if (strings.length == 0) {
                return new ArrayList<>();
            }

            if (commandSender.hasPermission(commandConfig.permission()) && !commandSender.hasPermission("xg7plugins.command.anti-tab-bypass")) {
                return new ArrayList<>();
            }

            if (strings.length > 1) {

                if (strings[0].equalsIgnoreCase("help")) {

                    return command.onTabComplete(commandSender,new CommandArgs(strings));
                }

                command = commands.get(plConfig.mainCommandName() + strings[0]);

                if (command == null) {
                    return new ArrayList<>();
                }

                strings = Arrays.copyOfRange(strings, 1, strings.length);

            }

        }

        if (commandSender.hasPermission(commandConfig.permission()) && !commandSender.hasPermission("xg7plugins.command.anti-tab-bypass")) {
            return new ArrayList<>();
        }

        return command.onTabComplete(commandSender,new CommandArgs(strings));
    }
}
