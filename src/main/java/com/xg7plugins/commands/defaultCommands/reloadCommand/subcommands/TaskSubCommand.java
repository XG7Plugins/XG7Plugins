package com.xg7plugins.commands.defaultCommands.reloadCommand.subcommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.temp.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@Command(
        name = "tasks",
        description = "Reload tasks Command",
        syntax = "/xg7plugins reload tasks (plugin)",
        permission = "xg7plugins.command.reload.tasks",
        isAsync = true
)
public class TaskSubCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Plugin plugin = XG7Plugins.getInstance();

        if (args.len() != 0) plugin = XG7Plugins.getInstance().getPlugins().get(args.get(0, String.class));

        XG7Plugins.taskManager().cancelTasks(plugin);
        XG7Plugins.taskManager().registerTasks(plugin.loadRepeatingTasks());

        Plugin finalPlugin = plugin;
        Text.formatLang(XG7Plugins.getInstance(),sender,"reload-message.tasks").thenAccept(text ->
                text.replace("[PLUGIN]", finalPlugin.getName())
                    .send(sender)
        );
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.BOOK, this);
    }
}
