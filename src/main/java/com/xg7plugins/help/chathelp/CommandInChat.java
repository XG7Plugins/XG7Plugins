package com.xg7plugins.help.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.event.ClickEvent;
import com.xg7plugins.utils.text.component.event.action.ClickAction;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class CommandInChat extends HelpPage {

    private final Plugin plugin;

    public CommandInChat(Plugin plugin, List<ICommand> commands, int page, int maxPage) {
        super("command-in-chat" + UUID.randomUUID());
        this.plugin = plugin;

        addMessages(
                HelpComponent.of(plugin, "&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-").build(),
                HelpComponent.of(plugin, "lang:[help-in-chat.commands-title]")
                        .replace("page", (page + 1) + "")
                        .replace("max_page", maxPage + "")
                        .build()
        );
        for (ICommand command : commands) {

            Item commandIcon = command.getIcon();

            addMessages(
                    HelpComponent.empty(),
                    new CommandComponent(plugin,command, commandIcon)
            );

        }

        addMessage(HelpComponent.of(plugin, "&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-").build());


    }

    public static class CommandComponent extends HelpComponent {

        private final Item commandIcon;
        private final ICommand command;

        public CommandComponent(Plugin plugin, ICommand command, Item commandIcon) {
            super(
                    plugin,
                    null
            );
            this.commandIcon = commandIcon;
            this.command = command;
            this.placeholders = commandIcon.getBuildPlaceholders();
        }

        @Override
        public Component buildFor(Player player) {
            ItemStack itemStack = commandIcon.getItemFor(player, XG7Plugins.getInstance());

            Component.Builder builder = Component.text(
                    itemStack.getItemMeta().getDisplayName() + "\n" +
                            itemStack.getItemMeta().getLore().get(0) + "\n" +
                            itemStack.getItemMeta().getLore().get(1) + "\n" +
                            itemStack.getItemMeta().getLore().get(2) + "\n" +
                            itemStack.getItemMeta().getLore().get(3)
            );

            builder.onClick(new ClickEvent(command.getClass().getAnnotation(Command.class).syntax(), ClickAction.SUGGEST_COMMAND));

            return builder.build();
        }

        @Override
        public Component build() {
            ItemStack itemStack = commandIcon.getItemStack();

            Component.Builder builder = Component.text(
                    itemStack.getItemMeta().getDisplayName() + "\n" +
                            itemStack.getItemMeta().getLore().get(0) + "\n" +
                            itemStack.getItemMeta().getLore().get(1) + "\n" +
                            itemStack.getItemMeta().getLore().get(2) + "\n" +
                            itemStack.getItemMeta().getLore().get(3)
            );

            builder.onClick(new ClickEvent(command.getClass().getAnnotation(Command.class).syntax(), ClickAction.SUGGEST_COMMAND));

            return builder.build();
        }
    }


}
