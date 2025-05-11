package com.xg7plugins.help.guihelp;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.MainCommand;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.gui.PageMenu;
import com.xg7plugins.modules.xg7menus.menus.holders.PageMenuHolder;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CommandMenu extends PageMenu {

    private final Map<String, Command> commands;
    private final CommandMenu superMenu;

    private final HelpCommandGUI guiOrigin;

    public CommandMenu(List<Command> commands, String customTitle, CommandMenu superMenu, HelpCommandGUI guiOrigin) {
        super(XG7Plugins.getInstance(), "command_menu" + UUID.randomUUID(), customTitle == null ? "Commands" : customTitle, 54, Slot.of(2,2), Slot.of(5,8));
        this.commands = commands.stream().collect(
                Collectors.toMap(
                        command -> command.getClass().getAnnotation(Command.class).name(),
                        command -> command
                )
        );
        this.guiOrigin = guiOrigin;
        this.superMenu = superMenu;

    }

    @Override
    public List<Item> pagedItems(Player player) {

        return commands
                .values()
                .stream()
                .filter(command -> !(command instanceof MainCommand))
                .map(command -> {
                    Item item = command.getIcon();
                    item.setNBTTag("command", command.getClass().getAnnotation(Command.class).name());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected List<Item> items(Player player) {
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

        switch (event.getClickedSlot()) {
            case 45:
                ((PageMenuHolder) event.getInventoryHolder()).previousPage();
                return;
            case 53:
                ((PageMenuHolder) event.getInventoryHolder()).nextPage();
                return;
            case 48:
                guiOrigin.getMenu("index").open((Player) event.getWhoClicked());
                return;
            case 50:
                if (superMenu == null) return;
                superMenu.open((Player) event.getWhoClicked());
                return;
            default:
                break;
        }

        Item item = event.getClickedItem();

        if (item.isAir()) return;

        Command command = commands.get(item.getTag("command", String.class).orElse(null));

        if (command == null) return;

        if (command.getSubCommands().length == 0) return;

        CommandMenu commandMenu = new CommandMenu(Arrays.asList(command.getSubCommands()), "Subcommands of: " + command.getClass().getAnnotation(Command.class).name(), this, guiOrigin);

        commandMenu.open((Player) event.getWhoClicked());



    }
}
