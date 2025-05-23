package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Index implements HelpChatPage {

    @Override
    public List<Text> getComponents(CommandSender sender) {

        List<Text> components = new ArrayList<>();


        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));

        components.add(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.title").join());

        components.add(Text.format(" "));

        Component content = Component.text(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.content").join().getText())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/xg7plugins help about"))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Click to see about the plugins")));
        components.add(Text.format(content));

        components.add(Text.format(" "));

        Component lang = Component.text(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.lang").join().getText())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/xg7plugins lang"));
        components.add(Text.format(lang));

        Component tasks = Component.text(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.tasks").join().getText())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/xg7plugins tasks"));
        components.add(Text.format(tasks));

        Component commands = Component.text(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.commands").join().getText())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/xg7plugins help command-page1"));
        components.add(Text.format(commands));

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));

        return components;

    }

    @Override
    public String getId() {
        return "index";
    }
}
