package com.xg7plugins.help.xg7pluginshelp;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.libs.xg7menus.item.BookItem;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.libs.xg7menus.item.SkullItem;
import com.xg7plugins.libs.xg7menus.menus.gui.Menu;
import com.xg7plugins.utils.text.Text;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class XG7PluginsHelpGUI extends Menu {

    private Plugin plugin;

    public XG7PluginsHelpGUI(Plugin plugin) {
        super(plugin, "help-command-index", "lang:[help-menu.index.title]", 45);
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected List<Item> items(Player player) {
        return Arrays.asList(
                SkullItem.newSkull().renderPlayerSkull(true).name("lang:[help-menu.index.profile-item.name]").lore("lang:[help-menu.index.profile-item.lang-chose]").slot(13),
                Item.from(XMaterial.BOOK).name("lang:[help-menu.index.lang-item.name]").lore("lang:[help-menu.index.lang-item.lore]").slot(29),
                Item.from(XMaterial.CLOCK).name("lang:[help-menu.index.tasks-item.name]").lore("lang:[help-menu.index.tasks-item.lore]").slot(30),
                Item.from(XMaterial.WRITABLE_BOOK).name("lang:[help-menu.index.about-item.name]").lore("lang:[help-menu.index.about-item.lore]").slot(31),
                Item.from(XMaterial.matchXMaterial("COMMAND_BLOCK").orElse(XMaterial.ENDER_PEARL)).name("lang:[help-menu.index.commands-item.name]").lore("lang:[help-menu.index.commands-item.lore]").slot(32),
                Item.from(XMaterial.PAPER).name("lang:[help-menu.index.see-in-chat]").slot(33)
        );
    }

    @Override
    public <T extends MenuEvent> void onClick(T event) {
        event.setCancelled(true);
        if (!(event instanceof ClickEvent)) return;

        ClickEvent clickEvent = (ClickEvent) event;

        Player player = (Player) clickEvent.getWhoClicked();

        switch (clickEvent.getClickedSlot()) {
            case 29:
                player.closeInventory();
                player.performCommand("lang");
                break;
            case 30:
                player.closeInventory();
                player.performCommand("tasks");
                break;
            case 31:

                Config lang = XG7Plugins.getInstance().getLangManager() == null ?
                      XG7Plugins.getInstance().getConfig("messages") :
                      Config.of(XG7Plugins.getInstance(), XG7Plugins.getInstance().getLangManager().getLangByPlayer(plugin, player).join());

                List<String> about = lang.get("help-menu.about", List.class).orElse(new ArrayList<String>());

                BookItem bookItem = BookItem.newBook();

                List<List<String>> pages = new ArrayList<>();
                List<String> currentPage = new ArrayList<>();

                for (String line : about) {

                    currentPage.add(Text.format(line, XG7Plugins.getInstance())
                            .replace("[DISCORD]", "discord.gg/jfrn8w92kF")
                            .replace("[GITHUB]", "github.com/DaviXG7")
                            .replace("[WEBSITE]", "xg7plugins.com")
                            .replace("[VERSION]", XG7Plugins.getInstance().getDescription().getVersion())
                            .getWithPlaceholders(player));
                    if (currentPage.size() == 10) {
                        System.out.println("Adding page");
                        pages.add(new ArrayList<>(currentPage));
                        System.out.println(pages);
                        currentPage.clear();
                    }
                }
                if (!currentPage.isEmpty()) {
                    pages.add(currentPage);
                }

                for (List<String> page : pages) {
                    bookItem.addPage(page.stream().collect(Collectors.joining("\n")));
                }

                player.closeInventory();
                bookItem.openBook(player);
                return;
            case 32:
                plugin.getHelpCommandGUI().getMenu("commands").open(player);
                break;
            case 33:
                player.closeInventory();
                plugin.getHelpInChat().sendPage("index", player);
                break;

        }

    }

}
