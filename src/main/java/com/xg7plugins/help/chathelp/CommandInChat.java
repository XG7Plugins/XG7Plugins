//package com.xg7plugins.help.chathelp;
//
//import com.xg7plugins.XG7Plugins;
//import com.xg7plugins.commands.setup.Command;
//import com.xg7plugins.commands.setup.ICommand;
//import net.md_5.bungee.api.chat.ClickEvent;
//import net.md_5.bungee.api.chat.TextComponent;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.UUID;
//
//public class CommandInChat extends HelpPage {
//
//
//    public CommandInChat(List<ICommand> commands, int page, int maxPage) {
//        super("command-in-chat" + UUID.randomUUID());
//
//        addMessage(new HelpComponent(
//                "&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-",
//                null,null
//        ));
//        addMessages(
//                new HelpComponent(
//                        new HashMap<String, String>() {
//                            {
//                                put("[PAGE]", (page + 1) + "");
//                                put("[MAX_PAGE]", maxPage + "");
//                            }
//                        },
//                        "lang:[help-in-chat.commands-title]",
//                        null,null
//                )
//        );
//        for (ICommand command : commands) {
//
//            Item commandIcon = command.getIcon();
//
//            addMessages(
//                    HelpComponent.empty(),
//                    new CommandComponent(command, commandIcon)
//            );
//
//        }
//
//        addMessage(new HelpComponent(
//                "&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-",
//                null,null
//        ));
//
//
//    }
//
//    public static class CommandComponent extends HelpComponent {
//
//        private final Item commandIcon;
//        private final ICommand command;
//
//        public CommandComponent(ICommand command, Item commandIcon) {
//            super(commandIcon.getItemStack().getItemMeta().getDisplayName() + "\n" +
//                    commandIcon.getItemStack().getItemMeta().getLore().get(0) + "\n" +
//                    commandIcon.getItemStack().getItemMeta().getLore().get(1) + "\n" +
//                    commandIcon.getItemStack().getItemMeta().getLore().get(2) + "\n" +
//                    commandIcon.getItemStack().getItemMeta().getLore().get(3), null, null);
//            this.commandIcon = commandIcon;
//            this.command = command;
//            this.placeholders = commandIcon.getBuildPlaceholders();
//        }
//
//        @Override
//        public TextComponent build(Player player) {
//            ItemStack itemStack = commandIcon.getItemFor(player, XG7Plugins.getInstance());
//
//            TextComponent textComponent = new TextComponent(
//                    itemStack.getItemMeta().getDisplayName() + "\n" +
//                            itemStack.getItemMeta().getLore().get(0) + "\n" +
//                            itemStack.getItemMeta().getLore().get(1) + "\n" +
//                            itemStack.getItemMeta().getLore().get(2) + "\n" +
//                            itemStack.getItemMeta().getLore().get(3)
//            );
//
//            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command.getClass().getAnnotation(Command.class).syntax()));
//
//            return textComponent;
//        }
//    }
//
//
//}
