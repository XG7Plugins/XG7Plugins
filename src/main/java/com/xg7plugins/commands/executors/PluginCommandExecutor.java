package com.xg7plugins.commands.executors;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.PluginSetup;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.tasks.tasks.AsyncTask;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class PluginCommandExecutor implements CommandExecutor, TabCompleter {

    private CommandManager manager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] strings) {

        PluginSetup plConfig = manager.getPlugin().getPluginSetup();

        Command command = manager.getCommand(cmd.getName());

        CommandSetup commandConfig = command.getCommandSetup();

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
            command = manager.getCommand(plConfig.mainCommandName() + strings[0]);
            if (command == null) {
                CommandMessages.COMMAND_NOT_FOUND.send(sender);
                return true;
            }
            strings = Arrays.copyOfRange(strings, 1, strings.length);
        }

        processCommand(command, sender, strings);

        return true;
    }

    /**
     * Recursively processes subcommands of a given command.
     * Traverses through command hierarchy to find and execute the appropriate subcommand.
     *
     * @param command Parent command
     * @param sender  Command sender
     * @param args    Command arguments
     * @param index   Current argument index
     * @return true if a subcommand was processed, false otherwise
     */
    public boolean processSubCommands(Command command, CommandSender sender, String[] args, int index) {
        if (command == null) return false;
        if (args.length == index) return false;

        List<Command> subCommands = command.getSubCommands();

        if (subCommands.isEmpty()) return false;

        Command subCommandChosen = null;

        for (Command subCommand : subCommands) {
            if (subCommand.getCommandSetup().name().equalsIgnoreCase(args[index])) {
                subCommandChosen = subCommand;
                break;
            }
        }

        if (subCommandChosen == null) return false;

        if (!subCommandChosen.getSubCommands().isEmpty() && processSubCommands(subCommandChosen, sender, args, index + 1)) return true;

        processCommand(subCommandChosen, sender, Arrays.copyOfRange(args, index + 1, args.length));

        return true;
    }

    /**
     * Processes a command execution with permission checks and sender validations.
     * Handles async execution if configured and manages command error handling.
     *
     * @param command Command to process
     * @param sender  Command sender
     * @param strings Command arguments
     */
    private void processCommand(Command command, CommandSender sender, String[] strings) {
        if (processSubCommands(command, sender, strings, 0)) return;

        CommandSetup commandConfig = command.getCommandSetup();

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
            if (commandConfig.isInEnabledWorldOnly() && !XG7PluginsAPI.isInAnEnabledWorld(manager.getPlugin(), ((Player) sender))) {
                CommandMessages.DISABLED_WORLD.send(sender);
                return;
            }
        }

        CommandArgs commandArgs = new CommandArgs(strings);

        if (commandConfig.isAsync()) {

            final Command finalCommand = command;

            XG7PluginsAPI.taskManager().runAsync(AsyncTask.of(manager.getPlugin(), "commands", () -> finalCommand.onCommand(sender,commandArgs)));

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

        PluginSetup plConfig = manager.getPlugin().getPluginSetup();

        Command command = manager.getCommand(cmd.getName());

        CommandSetup commandConfig = command.getCommandSetup();

        if (command instanceof MainCommand) {
            if (strings.length == 0) return Collections.emptyList();

            if (strings.length > 1) {

                if (strings[0].equalsIgnoreCase("help")) return command.onTabComplete(sender,new CommandArgs(strings));

                command = manager.getCommand(plConfig.mainCommandName() + strings[0]);

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
