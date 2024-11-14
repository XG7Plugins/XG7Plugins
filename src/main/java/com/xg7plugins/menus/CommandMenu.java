package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.commands.setup.ISubCommand;
import com.xg7plugins.commands.setup.SubCommand;
import com.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.menu.MenuBuilder;
import com.xg7plugins.libs.xg7menus.builders.menu.PageMenuBuilder;
import com.xg7plugins.libs.xg7menus.menus.gui.ItemsPageMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandMenu {


    public static void create(Plugin plugin, Player player) {


        if (XG7Plugins.getInstance().getMenuManager().cacheExistsPlayer("commands", player)) {
            ItemsPageMenu menu = (ItemsPageMenu) XG7Plugins.getInstance().getMenuManager().getMenuByPlayer("commands", player);
            menu.open();
            return;
        }

        List<BaseItemBuilder<?>> commands = plugin.getCommandManager().getCommands().values().stream().map(ICommand::getIcon).collect(Collectors.toList());


        PageMenuBuilder builder = MenuBuilder.page("commands")
                .title("lang:[commands-menu.title]")
                .rows(6)
                .setArea(Slot.of(2, 2), Slot.of(5, 8))
                .setItems(commands)
                .setItem(49, ItemBuilder.from(XMaterial.BARRIER.parseMaterial(), plugin).name("lang:[close-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).close()));
        int langSize = plugin.getLangManager().getLangs().asMap().size();

        if (langSize > 24) {
            builder.setItem(45, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-back-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).previousPage()));
            builder.setItem(53, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-next-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).nextPage()));
        }

        builder.build(player, plugin).open();

    }

    public static void createSubCommandMenu(Plugin plugin, Player player, ICommand command) {

        InventoryView inventory = player.getOpenInventory();

        String commandName = command instanceof ISubCommand ? command.getClass().getAnnotation(SubCommand.class).name() : command.getClass().getAnnotation(Command.class).name();

        if (XG7Plugins.getInstance().getMenuManager().cacheExistsPlayer("subcommands:" + commandName, player)) {
            ItemsPageMenu menu = (ItemsPageMenu) XG7Plugins.getInstance().getMenuManager().getMenuByPlayer("subcommands:" + commandName, player);
            menu.open();
            return;
        }

        List<BaseItemBuilder<?>> commands = Arrays.stream(command.getSubCommands()).map(ICommand::getIcon).collect(Collectors.toList());
        PageMenuBuilder builder = MenuBuilder.page("subcommands:" + commandName)
                .title(commandName)
                .rows(6)
                .setArea(Slot.of(2,2), Slot.of(5,8))
                .setItems(commands)
                .setItem(48, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-back-item]").click(event -> player.openInventory(inventory.getTopInventory())))
                .setItem(49, ItemBuilder.from(XMaterial.BARRIER.parseItem(), plugin).name("lang:[close-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).close()));
        int langSize = plugin.getLangManager().getLangs().asMap().size();
        if (langSize > 24) {
            builder.setItem(45, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-back-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).previousPage()));
            builder.setItem(53, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-next-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).nextPage()));
        }

        builder.build(player, plugin).open();

    }



}
