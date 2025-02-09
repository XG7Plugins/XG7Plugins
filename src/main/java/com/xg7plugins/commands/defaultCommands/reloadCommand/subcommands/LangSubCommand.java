package com.xg7plugins.commands.defaultCommands.reloadCommand.subcommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@Command(
        name = "lang",
        description = "Reload Lang Command",
        syntax = "/xg7plugins reload lang (plugin)",
        permission = "xg7plugins.command.reload.lang",
        isAsync = true
)
public class LangSubCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Plugin plugin = XG7Plugins.getInstance();

        if (args.len() != 0) plugin = XG7Plugins.getInstance().getPlugins().get(args.get(0, String.class));

        LangManager langManager = XG7Plugins.getInstance().getLangManager();

        langManager.getLangs().clear().join();
        langManager.loadLangsFrom(plugin);

        Plugin finalPlugin = plugin;
        Text.fromLang(sender,XG7Plugins.getInstance(),"reload-message.lang").thenAccept(text ->
                text.replace("[PLUGIN]", finalPlugin.getName())
                        .send(sender)
        );
    }

//    @Override
//    public Item getIcon() {
//        return Item.commandIcon(XMaterial.WRITABLE_BOOK, this);
//    }
}
