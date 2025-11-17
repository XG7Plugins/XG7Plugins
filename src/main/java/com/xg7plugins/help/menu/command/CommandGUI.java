package com.xg7plugins.help.menu.command;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.executors.MainCommand;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.help.menu.HelpGUI;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PagedMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.utils.item.Item;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandGUI extends PagedMenu {

    private final Map<String, CommandNode> commands;
    private final CommandGUI superMenu;

    private final HelpGUI guiOrigin;

    public CommandGUI(Plugin plugin, List<CommandNode> commands, String title, CommandGUI superMenu, HelpGUI guiOrigin, String commandName) {
        super(
                new CommandGUIConfiguration(commandName, plugin,title),
                Slot.of(2,2), Slot.of(4,8)
        );
        this.commands = commands.stream().collect(
                Collectors.toMap(
                        CommandNode::getName,
                        command -> command
                )
        );
        this.guiOrigin = guiOrigin;
        this.superMenu = superMenu;

    }

    public CommandGUI(Plugin plugin, List<CommandNode> commands, String title, CommandGUI superMenu, HelpGUI guiOrigin) {
        this(plugin, commands, title, superMenu, guiOrigin, null);
    }

    @Override
    public List<InventoryItem> pagedItems(Player player) {

        return commands
                .entrySet()
                .stream()
                .filter(e -> !(e.getValue().getCommand() instanceof MainCommand))
                .map(e -> {
                    CommandNode command = e.getValue();
                    XMaterial iconMaterial = command.getParent() == null? command.getCommand().getCommandSetup().iconMaterial() : command.getCommandMethod().getAnnotation(CommandConfig.class).iconMaterial();

                    InventoryItem item = Item.commandIcon(iconMaterial, command).toInventoryItem(null);
                    item.setNBTTag("command", e.getKey());

                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryItem> getItems(Player player) {
        return Arrays.asList(
                InventoryItem.from(XMaterial.ARROW).name("lang:[go-back-item]").toInventoryItem(45),
                InventoryItem.from(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR)).name("lang:[go-back-to-help]").toInventoryItem(48),
                InventoryItem.from(XMaterial.REDSTONE).name("lang:[go-back-to-previous-commands]").toInventoryItem(50),
                InventoryItem.from(XMaterial.ARROW).name("lang:[go-next-item]").toInventoryItem(53)
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
                InventoryItem item = event.getClickedItem();

                if (item.isAir()) return;

                CommandNode command = commands.get(item.getTag("command", String.class).orElse(null));

                if (command == null) return;

                if (command.getChildren().isEmpty()) return;

                CommandGUI commandMenu = new CommandGUI(guiOrigin.getPlugin(), command.getChildren(), "lang:[help-menu.command-help.subcommands-title]", this, guiOrigin, command.getName());

                commandMenu.open(holder.getPlayer());
                break;
        }

    }
}
