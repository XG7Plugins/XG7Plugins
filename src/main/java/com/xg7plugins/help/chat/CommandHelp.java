package com.xg7plugins.help.chat;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.event.ClickEvent;
import com.xg7plugins.utils.text.component.event.action.ClickAction;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CommandHelp implements HelpChatPage {

    @Getter
    private final Plugin plugin;

    private final List<Command> commands;

    private final int page;
    private final int maxPage;

    public CommandHelp(Plugin plugin, List<Command> commands, int page, int maxPage) {
        this.plugin = plugin;
        this.commands = commands;
        this.page = page;
        this.maxPage = maxPage;
    }

    @Override
    public List<Component> getComponents(CommandSender sender) {

        List<Component> components = new ArrayList<>();

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-").getComponent());
        components.add(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.commands-title").join()
                .replace("page", (page + 1) + "")
                .replace("max_page", maxPage + "")
                .getComponent());

        for (Command command : commands) {
            Item commandIcon = command.getIcon();

            ItemStack itemStack = commandIcon.getItemFor(sender, plugin);

            Component component = Component.text(
                    itemStack.getItemMeta().getDisplayName() + "\n" +
                    itemStack.getItemMeta().getLore().get(0) + "\n" +
                    itemStack.getItemMeta().getLore().get(1) + "\n" +
                    itemStack.getItemMeta().getLore().get(2) + "\n" +
                    itemStack.getItemMeta().getLore().get(3)
            ).onClick(new ClickEvent(command.getCommandConfigurations().syntax(), ClickAction.SUGGEST_COMMAND)).build();

            components.add(component);
        }

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-").getComponent());

        return components;
    }

    @Override
    public String getId() {
        return "command_page-" + page;
    }


}
