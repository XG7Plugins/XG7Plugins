package com.xg7plugins.commands.core_commands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.holders.PagedMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.menus.PagedMenu;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CommandSetup(
        name = "test", description = "", syntax = "", pluginClass = XG7Plugins.class
)
public class TestCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Text.format("[CENTER:CHAT] &aSou legau <click:suggest_command:Comando>Click aqui</click> <hover:show_text:mesmo n><rainbow>Aqui não</rainbow></hover> <click:suggest_command:aaaaa><hover:show_text:aaaaaaaaa>Nem aqui</hover></click>").send(sender);

        Text.getAudience().player((Player) sender).sendMessage(Component.text("Outro teste").clickEvent(ClickEvent.suggestCommand("AAAAAAAAAAAAAAAA")));

        XG7Menus.getInstance().registerMenus(new TestPagedMenu());

        XG7Menus.getInstance().getMenu(XG7Plugins.getInstance(), "iafdasoidf").open((Player) sender);

    }

    @Override
    public Item getIcon() {
        return null;
    }

    class TestPagedMenu extends PagedMenu {

        public TestPagedMenu() {
            super(
                    MenuConfigurations.of(
                            XG7Plugins.getInstance(),
                            "iafdasoidf",
                            "Title",
                            6
                    ), Slot.of(2,2), Slot.of(5,8)
            );
        }

        @Override
        public List<Item> pagedItems(Player player) {
            return IntStream.range(0, 100).mapToObj(i -> Item.from(XMaterial.STONE).name("Item: " + i)).collect(Collectors.toList());
        }

        @Override
        public List<Item> getItems(Player player) {
            return Collections.singletonList(Item.from(XMaterial.STONE).slot(50));
        }

        @Override
        public void onClick(ActionEvent event) {
            event.getHolder().getPlayer().sendMessage(event.getClickedItem().toString());

            PagedMenuHolder menuHolder = (PagedMenuHolder) event.getHolder();

            if (event.getClickedSlot().equals(6,9)) {
                menuHolder.nextPage();
            }
            if (event.getClickedSlot().equals(6,1)) {
                menuHolder.previousPage();
            }
            if (event.getClickedSlot().equals(6,5)) {
                PagedMenu.refresh(menuHolder);
            }
        }
    }

}
