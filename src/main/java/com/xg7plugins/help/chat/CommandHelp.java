package com.xg7plugins.help.chat;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.ClickEvent;
import com.xg7plugins.utils.text.component.TextComponentBuilder;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CommandHelp implements HelpChatPage {

    @Getter
    private final Plugin plugin;

    private final List<CommandNode> commands;

    private final int page;
    private final int maxPage;

    public CommandHelp(Plugin plugin, List<CommandNode> commands, int page, int maxPage) {
        this.plugin = plugin;
        this.commands = commands;
        this.page = page;
        this.maxPage = maxPage;
    }

    @Override
    public List<Text> getComponents(CommandSender sender) {

        List<Text> components = new ArrayList<>();

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));
        components.add(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.commands-title")
                .replace("page", (page + 1) + "")
                .replace("max_page", maxPage + ""));

        for (CommandNode command : commands) {
            Item commandIcon = Item.commandIcon(command.getCommand().getCommandSetup().iconMaterial(), command);

            ItemStack itemStack = commandIcon.getItemFor(sender, XG7Plugins.getInstance());

            components.add(Text.format("&m-&9&m-&6&m---------&e*&6&m---------&9&m-&f&m-"));

            ClickEvent clickEvent = ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, command.getCommand().getCommandSetup().syntax());

            components.add(
                    TextComponentBuilder.of(itemStack.getItemMeta().getDisplayName()).clickEvent(clickEvent).append("<br>")
                            .append(itemStack.getItemMeta().getLore().get(0)).clickEvent(clickEvent).append("<br>")
                            .append(itemStack.getItemMeta().getLore().get(1)).clickEvent(clickEvent).append("<br>")
                            .append(itemStack.getItemMeta().getLore().get(2)).clickEvent(clickEvent).append("<br>")
                            .append(itemStack.getItemMeta().getLore().get(3)).clickEvent(clickEvent).append("<br>")
                            .append("Subcommands: " + !command.getChildren().isEmpty()).clickEvent(clickEvent)
                            .build()
            );

            components.add(Text.format("&m-&9&m-&6&m---------&e*&6&m---------&9&m-&f&m-"));
            components.add(Text.format(""));
        }

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));

        return components;
    }

    @Override
    public String getId() {
        return "command_page-" + page;
    }


}
