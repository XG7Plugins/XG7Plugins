package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.ClickEvent;
import com.xg7plugins.utils.text.component.HoverEvent;
import com.xg7plugins.utils.text.component.TextComponentBuilder;
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

        Text content = TextComponentBuilder.of(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.content").join().getText())
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7plugins help about"))
                .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, "Click to see about the plugins"))
                .build();
        components.add(content);

        components.add(Text.format(" "));

        Text lang = TextComponentBuilder.of(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.lang").join().getText())
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7plugins lang"))
                .build();
        components.add(lang);

        Text tasks = TextComponentBuilder.of(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.tasks").join().getText())
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7plugins tasks"))
                .build();
        components.add(tasks);

        Text commands = TextComponentBuilder.of(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.commands").join().getText())
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7plugins help command-page1"))
                .build();
        components.add(commands);

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));

        return components;

    }

    @Override
    public String getId() {
        return "index";
    }
}
