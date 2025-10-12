package com.xg7plugins.commands.executors;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.setup.PluginSetup;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.CommandState;
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

/**
 * The executor for all XG7Plugins' commands.
 * <p>
 * This class handles the execution and tab-completion of all registered plugin commands,
 * including dynamic subcommand resolution for the main command entry point.
 * It also applies permission checks and custom error messaging when needed.
 */
@AllArgsConstructor
public class PluginCommandExecutor implements CommandExecutor, TabCompleter {

    private CommandManager manager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] strings) {

        PluginSetup plConfig = manager.getPlugin().getPluginSetup();

        Command command = manager.getCommand(cmd.getName());

        CommandSetup commandConfig = command.getCommandSetup();

        if (command instanceof MainCommand) {
            if (!sender.hasPermission(commandConfig.permission()) && !sender.hasPermission("xg7plugins.command.anti-tab-bypass")) {
                CommandState.COMMAND_NOT_FOUND.send(sender);
                return true;
            }
            if (!sender.hasPermission(commandConfig.permission())) {
                CommandState.NO_PERMISSION.send(sender);
                return true;
            }
            if (strings.length == 0) {
                CommandState.syntaxError(commandConfig.syntax()).send(sender);
                return true;
            }

            if (strings[0].equalsIgnoreCase("help")) {
                command.onCommand(sender,new CommandArgs(strings));
                return true;
            }
            command = manager.getCommand(plConfig.mainCommandName() + strings[0]);
            if (command == null) {
                CommandState.COMMAND_NOT_FOUND.send(sender);
                return true;
            }
            strings = Arrays.copyOfRange(strings, 1, strings.length);
        }

        if (!sender.hasPermission(commandConfig.permission()) && !sender.hasPermission("xg7plugins.command.anti-tab-bypass")) {
            CommandState.NO_PERMISSION.send(sender);
            return true;
        }

        processCommand(command, sender, strings);

        return true;
    }

    /**
     * Recursively processes subcommands of a given command.
     * Traverses through the command hierarchy to find and execute the appropriate subcommand.
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
            CommandState.NO_PERMISSION.send(sender);
            return;
        }
        if (commandConfig.isPlayerOnly() && !(sender instanceof Player)) {
            CommandState.NOT_A_PLAYER.send(sender);
            return;
        }
        if (commandConfig.isConsoleOnly() && sender instanceof Player) {
            CommandState.IS_A_PLAYER.send(sender);
            return;
        }
        if (sender instanceof Player) {
            if (commandConfig.isInEnabledWorldOnly() && !XG7PluginsAPI.isInAnEnabledWorld(manager.getPlugin(), ((Player) sender))) {
                CommandState.DISABLED_WORLD.send(sender);
                return;
            }
        }

        CommandArgs commandArgs = new CommandArgs(strings);

        if (commandConfig.isAsync()) {

            final Command finalCommand = command;

            XG7PluginsAPI.taskManager().runAsync(AsyncTask.of("commands", () -> {
                CommandState state = finalCommand.onCommand(sender,commandArgs);
                state.send(sender);

                command.getPlugin().getDebug().info("Returned state: " + state);
            }));

            return;
        }

        try {

            command.getPlugin().getDebug().info(sender.getName() + " is executing: /" + command.getCommandSetup().name());

            CommandState state = command.onCommand(sender,commandArgs);
            
            state.send(sender);

            command.getPlugin().getDebug().info("Returned state: " + state);
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
            if (strings.length == 0 || !sender.hasPermission("xg7plugins.command")) return Collections.emptyList();

            if (strings.length > 1) {

                if (strings[0].equalsIgnoreCase("help")) return command.onTabComplete(sender,new CommandArgs(strings));

                command = manager.getCommand(plConfig.mainCommandName() + strings[0]);

                if (command == null) return Collections.emptyList();

                strings = Arrays.copyOfRange(strings, 1, strings.length);
            }

        }

        if (!sender.hasPermission(commandConfig.permission()) && !sender.hasPermission("xg7plugins.command.anti-tab-bypass")) {
            return Collections.emptyList();
        }

        return command.onTabComplete(sender,new CommandArgs(strings));
    }

}
