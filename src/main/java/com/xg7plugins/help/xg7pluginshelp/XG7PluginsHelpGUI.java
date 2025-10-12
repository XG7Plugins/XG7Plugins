package com.xg7plugins.help.xg7pluginshelp;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.OpenBookClickableItem;
import com.xg7plugins.modules.xg7menus.item.impl.BookItem;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.item.impl.SkullItem;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class XG7PluginsHelpGUI extends Menu {

    private final Plugin plugin;

    public XG7PluginsHelpGUI(Plugin plugin) {
        super(MenuConfigurations.of(plugin, "help-command-index", "lang:[help-menu.index.title]", 5));
        this.plugin = plugin;
    }

    @Override
    public List<Item> getItems(Player player) {

        ConfigSection lang = XG7PluginsAPI.langManager().getLangByPlayer(plugin, player).join().getSecond().getLangConfiguration();

        List<String> about = lang.getList("help-menu.about", String.class).orElse(new ArrayList<>());

        return Arrays.asList(

                SkullItem.newSkull().renderPlayerSkull(true)
                        .name("lang:[help-menu.index.profile-item.name]")
                        .lore("lang:[help-menu.index.profile-item.lang-chose]")
                        .slot(13),


                Item.from(XMaterial.BOOK)
                        .name("lang:[help-menu.index.lang-item.name]")
                        .lore("lang:[help-menu.index.lang-item.lore]")
                        .slot(29)
                        .clickable(actionEvent -> player.performCommand("lang")),

                Item.from(XMaterial.CLOCK)
                        .name("lang:[help-menu.index.tasks-item.name]")
                        .lore("lang:[help-menu.index.tasks-item.lore]")
                        .slot(30)
                        .clickable(actionEvent -> player.performCommand("tasks")),

                OpenBookClickableItem.get(
                                Item.from(XMaterial.WRITABLE_BOOK)
                                        .name("lang:[help-menu.index.about-item.name]")
                                        .lore("lang:[help-menu.index.about-item.lore]"),
                                player,
                                about
                        )
                        .slot(31),

                Item.from(XMaterial.matchXMaterial("COMMAND_BLOCK").orElse(XMaterial.ENDER_PEARL))
                        .name("lang:[help-menu.index.commands-item.name]")
                        .lore("lang:[help-menu.index.commands-item.lore]")
                        .slot(32)
                        .clickable(actionEvent -> plugin.getHelpMessenger().getGui().getMenu("commands").open(player)),

                Item.from(XMaterial.PAPER)
                        .name("lang:[help-menu.index.see-in-chat]")
                        .slot(33)
                        .clickable(actionEvent -> { player.closeInventory(); plugin.getHelpMessenger().getChat().send(player); }),

                Item.from(XMaterial.PAPER)
                        .name("lang:[collaborators-menu.collaborators-item.name]")
                        .lore("lang:[collaborators-menu.collaborators-item.lore]")
                        .slot(44)
                        .clickable(actionEvent -> plugin.getHelpMessenger().getGui().getMenu("collaborators").open(player))
        );
    }

}
