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

        components.add(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.title"));

        components.add(Text.format(" "));

        Text content = Text.format(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.content").getComponent()
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand( "/xg7plugins help about"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to see about the plugins"))));
        components.add(content);

        components.add(Text.format(" "));

        Text lang = Text.format(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.lang").getComponent()
                .clickEvent(ClickEvent.suggestCommand( "/xg7plugins lang")));

        components.add(lang);

        Text tasks = Text.format(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.tasks").getComponent()
                .clickEvent(ClickEvent.suggestCommand( "/xg7plugins tasks")));
        components.add(tasks);

        Text commands = Text.format(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.commands").getComponent()
                .clickEvent(ClickEvent.suggestCommand( "/xg7plugins help command-page1")));
        components.add(commands);

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));

        return components;

    }

    @Override
    public String getId() {
        return "index";
    }
}
