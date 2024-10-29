package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.menus.LangMenu;
import org.bukkit.entity.Player;

@Command(
        name = "xg7pluginlang",
        description = "Sets the language of the player",
        syntax = "/xg7pluginlang",
        aliasesPath = "lang",
        perm = "xg7plugins.command.lang",
        isOnlyPlayer = true
)
public class LangCommand implements ICommand {
    @Override
    public ItemBuilder getIcon() {
        return ItemBuilder.commandIcon(XMaterial.WRITABLE_BOOK, this, XG7Plugins.getInstance());
    }


    public void onCommand(org.bukkit.command.Command command, Player player, String label) {
        LangMenu.create(player);
    }
}
