package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.newxg7menus.Slot;
import com.xg7plugins.libs.newxg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.newxg7menus.events.ClickEvent;
import com.xg7plugins.libs.newxg7menus.events.DragEvent;
import com.xg7plugins.libs.newxg7menus.events.MenuEvent;
import com.xg7plugins.libs.newxg7menus.item.Item;
import com.xg7plugins.libs.newxg7menus.menus.gui.ConfirmationMenu;
import com.xg7plugins.libs.newxg7menus.menus.player.PlayerMenu;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(
        name = "test",
        description = "test",
        syntax = "test",
        aliasesPath = "test"
)
public class TestCommand implements ICommand {
    @Override
    public ItemBuilder getIcon() {
        return null;
    }

    @Override
    public void onCommand(org.bukkit.command.Command command, org.bukkit.command.CommandSender sender, String label) {
        Text.format("&aCCCCCCCCC", XG7Plugins.getInstance()).send(sender);

        try {
            new TestMenu().open((Player) sender);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public class TestMenu extends PlayerMenu {

        public TestMenu() {
            super(
                    XG7Plugins.getInstance(),
                    "Test Menu",
                    true
            );
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void onOpen(MenuEvent event) {
            event.getWhoClicked().sendMessage("Test Menu Opened");
        }

        @Override
        public void onClose(MenuEvent event) {
            event.getWhoClicked().sendMessage("Test Menu Closed");
        }

        @Override
        public <T extends MenuEvent> void onClick(T event) {

            Player player = (Player) event.getWhoClicked();

            if (event instanceof ClickEvent) {
                ClickEvent clickEvent = (ClickEvent) event;
                if (clickEvent.getClickedSlot() == 0) {
                    clickEvent.getWhoClicked().sendMessage("You clicked on the redstone");
                } else if (clickEvent.getClickedSlot() == 1) {
                    clickEvent.getWhoClicked().sendMessage("You clicked on the diamond");
                }

                if (clickEvent.getClickedSlot() == 0) {
                    event.getWhoClicked().sendMessage("Clicou!!!!!!!!!");
                }
                if (clickEvent.getClickedSlot() == 8) {
                    event.getWhoClicked().sendMessage("CLICOU TBM !!!!!!!!!!");
                }

                player.sendMessage("");
                player.sendMessage("Diagnostic");
                player.sendMessage("Clicked Slot: " + clickEvent.getClickedSlot());
                player.sendMessage("Raw Slot: " + clickEvent.getClickedRawSlot());
                player.sendMessage("Item: " + (clickEvent.getClickedItem().getItemStack() == null ? "null" : clickEvent.getClickedItem().getItemStack().getType()));
                player.sendMessage("Action: " + clickEvent.getClickAction());

                event.setCancelled(true);

                return;
            }

            if (event instanceof DragEvent) {
                DragEvent dragEvent = (DragEvent) event;

                player.sendMessage("");
                player.sendMessage("Diagnostic");
                player.sendMessage("Dragged Slots: " + dragEvent.getDraggedSlots().toString());
                player.sendMessage("Raw Slots: " + dragEvent.getDraggedRawSlots());
                player.sendMessage("Items: " + Arrays.toString(dragEvent.getDraggedItems().stream().map(item -> item.getItemStack() == null ? "null" : item.getItemStack().getType()).toArray()));
                player.sendMessage("Action: " + dragEvent.getClickAction());

                event.setCancelled(true);
            }

        }

        @Override
        protected List<Item> items() {
            return Arrays.asList(
                    Item.from(XMaterial.REDSTONE).slot(0).name("AAAAA").lore("BBBBBBBBBB"),
                    Item.from(XMaterial.DIAMOND).slot(8),
                    Item.from(XMaterial.DIAMOND_BLOCK).slot(4).clickable().onClick((event) -> {
                        close((Player) event.getWhoClicked());

                        event.getWhoClicked().sendMessage("FECOHUUIFEAUOAHSOD");
                    })
            );
        }

        @Override
        public void onDrop(MenuEvent event) {
            event.getWhoClicked().sendMessage("Dropei");
        }

        @Override
        public void onPickup(MenuEvent event) {
            event.getWhoClicked().sendMessage("Peguei");
        }

        @Override
        public void onBreak(MenuEvent event) {
            event.getWhoClicked().sendMessage("Quebrei");
        }

        @Override
        public void onPlace(MenuEvent event) {
            event.getWhoClicked().sendMessage("Coloquei");
        }
    }
}
