package com.xg7plugins.commands;

import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionMethod;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class CommandManager implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    @Getter
    private final HashMap<String, ICommand> commands = new HashMap<>();

    public void registerCommands(ICommand... commands) {

        plugin.getLog().loading("Loading Commands...");

        CommandMap commandMap = ReflectionObject.of(Bukkit.getServer()).getField("commandMap");

        Arrays.stream(commands).forEach(command -> {

            if (command == null) return;

            if (!command.isEnabled()) return;

            if (!command.getClass().isAnnotationPresent(Command.class)) {
                plugin.getLog().severe("Commands must be annotated with @Command interface!!");
                return;
            }

            Command commandSetup = command.getClass().getAnnotation(Command.class);

            String aliases = plugin.getConfigsManager().getConfig("commands").get(commandSetup.aliasesPath());
            if (aliases == null) return;

            PluginCommand pluginCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                    .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                    .newInstance(commandSetup.name(), plugin)
                    .getObject();

            if (!aliases.isEmpty()) pluginCommand.setAliases(Arrays.asList(aliases.split(", ")));


            pluginCommand.setExecutor(this);
            pluginCommand.setDescription(commandSetup.description());
            pluginCommand.setUsage(commandSetup.syntax());
            pluginCommand.setTabCompleter(this);
            commandMap.register(commandSetup.name(), pluginCommand);

            this.commands.put(commandSetup.name(), command);

        });

        plugin.getLog().loading("Successfully loaded Commands!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] strings) {

        XG7Plugins.getInstance().getTaskManager().runTask(() -> {

            ICommand command = commands.get(cmd.getName());

            if (processSubCommands(commandSender, command.getSubCommands(), strings, s, 0)) return;

            if (strings.length != 0) {
                Text.format("lang:[commands.syntax-error]",XG7Plugins.getInstance())
                        .replace("[SYNTAX]", cmd.getUsage())
                        .send(commandSender);
                return;
            }

            Command commandConfig = command.getClass().getAnnotation(Command.class);

            if (!commandSender.hasPermission(commandConfig.perm()) && !commandConfig.perm().isEmpty()) {
                Text.format("lang:[commands.no-permission]",XG7Plugins.getInstance()).send(commandSender);
                return;
            }

            if (commandConfig.isOnlyPlayer() && !(commandSender instanceof Player)) {
                Text.format("lang:[commands.not-a-player]",XG7Plugins.getInstance()).send(commandSender);
                return;
            }
            if (commandConfig.isOnlyConsole() && commandSender instanceof Player) {
                Text.format("lang:[commands.is-a-player]",XG7Plugins.getInstance()).send(commandSender);
                return;
            }
            if (commandSender instanceof Player) {
                if (!commandConfig.isOnlyInWorld() && plugin.getEnabledWorlds().contains(((Player) commandSender).getWorld().getName()) && !plugin.getEnabledWorlds().isEmpty()) {
                    Text.format("lang:[commands.disabled-world]",XG7Plugins.getInstance()).send(commandSender);
                    return;
                }
            }

            if (commandConfig.isOnlyPlayer()) {
                command.onCommand(cmd,(Player) commandSender,s);
                return;
            }

            command.onCommand(cmd,commandSender,s);

        });

        return true;
    }

    @SuppressWarnings("deprecated")
    private boolean processSubCommands(CommandSender sender, ISubCommand[] subCommands, String[] args, String label, int argsIndex) {

        if (subCommands == null) return false;

        if (args.length != argsIndex) {
            for (ISubCommand subCommand : subCommands) {

                SubCommand subCommandConfig = subCommand.getClass().getAnnotation(SubCommand.class);

                if (subCommandConfig == null) {
                    plugin.getLog().severe("Normal subcommands must be annotated with @SubCommandConfig to setup the subcommand!!");
                    continue;
                }

                if (!sender.hasPermission(subCommandConfig.perm()) && !subCommandConfig.perm().isEmpty()) {
                    Text.format("lang:[commands.no-permission]",XG7Plugins.getInstance()).send(sender);
                    return true;
                }
                if (subCommandConfig.isOnlyPlayer() && !(sender instanceof Player)) {
                    Text.format("lang:[commands.not-a-player]",XG7Plugins.getInstance()).send(sender);
                    return true;
                }
                if (subCommandConfig.isOnlyConsole() && sender instanceof Player) {
                    Text.format("lang:[commands.is-a-player]",XG7Plugins.getInstance()).send(sender);
                    return true;
                }
                if (sender instanceof Player) {
                    if (!subCommandConfig.isOnlyInWorld() && plugin.getEnabledWorlds().contains(((Player) sender).getWorld().getName()) && !plugin.getEnabledWorlds().isEmpty()) {
                        Text.format("lang:[commands.disabled-world]",XG7Plugins.getInstance()).send(sender);
                        return true;
                    }
                }
                if (subCommandConfig.type() == SubCommandType.ARGS) {
                    subCommand.onSubCommand(sender,args,label);
                    return true;
                }

                if (subCommand.getSubCommands().length != 0) return processSubCommands(sender, subCommand.getSubCommands(), args, label, argsIndex + 1);

                switch (subCommandConfig.type()) {
                    case NORMAL:

                        if (!subCommandConfig.name().equalsIgnoreCase(args[argsIndex])) {
                            continue;
                        }

                        subCommand.onSubCommand(sender,args,label);
                        return true;
                    case PLAYER:

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[argsIndex]);

                        if (!player.hasPlayedBefore()) {
                            Text.format("lang:[commands.never-played]",XG7Plugins.getInstance()).send(sender);
                            return true;
                        }

                        subCommand.onSubCommand(sender,player,label);
                        return true;
                    case OPTIONS:

                        Set<String> ops = ReflectionMethod.of(subCommand,"getOptions").invoke();

                        if (ops.stream().map(String::toLowerCase).noneMatch(s -> s.equals(args[argsIndex].toLowerCase()))) continue;


                        subCommand.onSubCommand(sender,args,label,args[argsIndex]);
                        return true;
                }
            }
        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] strings) {
        return commands.get(cmd.getName()).onTabComplete(cmd,commandSender,s,strings);
    }
}
