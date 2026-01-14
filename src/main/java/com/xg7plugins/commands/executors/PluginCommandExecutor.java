package com.xg7plugins.commands.executors;

import com.xg7plugins.boot.setup.PluginSetup;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.tasks.tasks.AsyncTask;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
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
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull org.bukkit.command.Command cmd,
                             @NotNull String label,
                             @NotNull String[] args) {

        PluginSetup plConfig = manager.getPlugin().getPluginSetup();
        CommandNode commandNode = manager.getRootCommandNode(cmd.getName());

        if (commandNode == null) {
            CommandState.COMMAND_NOT_FOUND.send(sender);
            return true;
        }

        CommandSetup commandConfig = commandNode.getCommand().getCommandSetup();

        if (!sender.hasPermission(commandConfig.permission()) && !commandConfig.permission().isEmpty()) {
            CommandState.NO_PERMISSION.send(sender);
            return true;
        }

        if (commandNode.getCommand() instanceof MainCommand) {
            if (args.length == 0) {
                CommandState.syntaxError(commandConfig.syntax()).send(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                commandNode.execute(sender, new CommandArgs(args));
                return true;
            }

            commandNode = manager.getRootCommandNode(plConfig.mainCommandName() + args[0]);
            if (commandNode == null) {
                CommandState.COMMAND_NOT_FOUND.send(sender);
                return true;
            }

            args = Arrays.copyOfRange(args, 1, args.length);
        }

        CommandNode chosen = commandNode;
        int depth = -1;

        for (int i = 0; i < args.length; i++) {
            String sub = args[i];
            CommandNode child = chosen.getChild(sub);

            if (child == null) break;

            chosen = child;
            depth = i;
        }

        if (chosen.getCommandMethod() == null) {
            CommandState.syntaxError(commandConfig.syntax()).send(sender);
            return true;
        }

        String[] remaining = depth + 1 >= args.length ? new String[0] : Arrays.copyOfRange(args, depth + 1, args.length);
        CommandArgs commandArgs = new CommandArgs(remaining);

        CommandConfig nodeConfig =
                chosen.getCommandMethod().getAnnotation(CommandConfig.class);

        if (!sender.hasPermission(nodeConfig.permission()) && !nodeConfig.permission().isEmpty()) {
            CommandState.NO_PERMISSION.send(sender);
            return true;
        }

        if (nodeConfig.isPlayerOnly() && !(sender instanceof Player)) {
            CommandState.NOT_A_PLAYER.send(sender);
            return true;
        }

        if (nodeConfig.isConsoleOnly() && sender instanceof Player) {
            CommandState.IS_A_PLAYER.send(sender);
            return true;
        }

        if (sender instanceof Player) {
            if (nodeConfig.isInEnabledWorldOnly() &&
                    !XG7Plugins.getAPI().isInAnEnabledWorld(manager.getPlugin(), ((Player) sender))) {
                CommandState.DISABLED_WORLD.send(sender);
                return true;
            }
        }

        Runnable commandRun = getCommandRun(sender, chosen, commandArgs);

        if (nodeConfig.isAsync()) {
            XG7Plugins.getAPI().taskManager().runAsync(AsyncTask.of("commands", commandRun));
            return true;
        }

        commandRun.run();

        return true;
    }

    private @NotNull Runnable getCommandRun(@NotNull CommandSender sender, CommandNode chosen, CommandArgs commandArgs) {
        return () -> {

            manager.getPlugin().getDebug().info("commands", "Executing /" +
                    chosen.getCommand().getCommandSetup().name() +
                    " (" + chosen.getName() + ") by " + sender.getName());

            try {

                CommandState state = chosen.execute(sender, commandArgs);

                if (state.equals(CommandState.SYNTAX_ERROR)) {
                    state = CommandState.syntaxError(chosen.getCommandMethod() == null || chosen.getCommand().getCommandSetup().syntax().isEmpty() ?
                            chosen.getCommand().getCommandSetup().syntax() :
                            chosen.getCommandMethod().getAnnotation(CommandConfig.class).syntax()
                    );
                }

                state.send(sender);

                manager.getPlugin().getDebug().info("command", "Returned state: " + state);

            } catch (Throwable e) {

                if (e instanceof NumberFormatException) CommandState.typeError("Number");
                else CommandState.ERROR.send(sender);

                e.printStackTrace();
            }
        };
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] strings) {

        PluginSetup plConfig = manager.getPlugin().getPluginSetup();

        CommandNode commandNode = manager.getRootCommandNode(cmd.getName());

        CommandSetup commandConfig = commandNode.getCommand().getCommandSetup();

        if (commandNode.getCommand() instanceof MainCommand) {
            if (strings.length == 0 || !sender.hasPermission("xg7plugins.command")) return Collections.emptyList();

            if (strings.length > 1) {

                if (strings[0].equalsIgnoreCase("help")) return commandNode.getCommand().onTabComplete(sender,new CommandArgs(strings));

                commandNode = manager.getRootCommandNode(plConfig.mainCommandName() + strings[0]);

                if (commandNode == null) return Collections.emptyList();

                strings = Arrays.copyOfRange(strings, 1, strings.length);
            }

        }

        if (!sender.hasPermission(commandConfig.permission()) && !sender.hasPermission("xg7plugins.command.anti-tab-bypass")) {
            return Collections.emptyList();
        }

        return commandNode.getCommand().onTabComplete(sender,new CommandArgs(strings));
    }

}
