package com.xg7plugins.help.menu.command;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.executors.MainCommand;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.help.menu.HelpGUI;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.holders.PagedMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.menus.PagedMenu;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandGUI extends PagedMenu {

    private final Map<String, Command> commands;
    private final CommandGUI superMenu;

    private final HelpGUI guiOrigin;

    public CommandGUI(Plugin plugin, List<Command> commands, String title, CommandGUI superMenu, HelpGUI guiOrigin, String commandName) {
        super(
                new CommandGUIConfiguration(commandName, plugin,title),
                Slot.of(2,2), Slot.of(5,8)
        );
        this.commands = commands.stream().collect(
                Collectors.toMap(
                        command -> command.getCommandConfigurations().name(),
                        command -> command
                )
        );
        this.guiOrigin = guiOrigin;
        this.superMenu = superMenu;

    }

    public CommandGUI(Plugin plugin, List<Command> commands, String title, CommandGUI superMenu, HelpGUI guiOrigin) {
        this(plugin, commands, title, superMenu, guiOrigin, null);
    }

    @Override
    public List<Item> pagedItems(Player player) {

        return commands
                .entrySet()
                .stream()
                .filter(e -> !(e.getValue() instanceof MainCommand))
                .map(e -> {
                    Item item = e.getValue().getIcon();
                    item.setNBTTag("command", e.getKey());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItems(Player player) {
        return Arrays.asList(
                Item.from(XMaterial.ARROW).name("lang:[go-back-item]").slot(45),
                Item.from(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR)).name("lang:[go-back-to-help]").slot(48),
                Item.from(XMaterial.REDSTONE).name("lang:[go-back-to-previous-commands]").slot(50),
                Item.from(XMaterial.ARROW).name("lang:[go-next-item]").slot(53)
        );
    }

    @Override
    public void onClick(ActionEvent event) {
        event.setCancelled(true);

        PagedMenuHolder holder = (PagedMenuHolder) event.getHolder();

        switch (event.getClickedSlot().get()) {
            case 45:
                holder.previousPage();
                return;
            case 53:
                holder.nextPage();
                return;
            case 48:
                guiOrigin.getMenu("index").open(holder.getPlayer());
                return;
            case 50:
                if (superMenu == null) return;
                superMenu.open(holder.getPlayer());
                return;
            default:
                Item item = event.getClickedItem();

                if (item.isAir()) return;

                Command command = commands.get(item.getTag("command", String.class).orElse(null));

                if (command == null) return;

                if (command.getSubCommands().isEmpty()) return;

                CommandGUI commandMenu = new CommandGUI(guiOrigin.getPlugin(), command.getSubCommands(), "lang:[help-menu.command-help.subcommands-title]", this, guiOrigin, command.getCommandConfigurations().name());

                commandMenu.open(holder.getPlayer());
                break;
        }

    }
}
