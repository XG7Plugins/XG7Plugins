package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.event.ClickEvent;
import com.xg7plugins.utils.text.component.event.HoverEvent;
import com.xg7plugins.utils.text.component.event.action.ClickAction;
import com.xg7plugins.utils.text.component.event.action.HoverAction;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Index implements HelpChatPage {

    @Override
    public List<Component> getComponents(CommandSender sender) {

        List<Component> components = new ArrayList<>();

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-").getComponent());

        components.add(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.title").join()
                .getComponent());

        components.add(Component.EMPTY);

        Component content = Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.content").join().getComponent();
        content.setClickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins help about"));
        content.setHoverEvent(HoverEvent.of(HoverAction.SHOW_TEXT, "Click to see about the plugins"));
        components.add(content);

        components.add(Component.EMPTY);

        Component lang = Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.lang").join().getComponent();
        lang.setClickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins lang"));
        components.add(lang);

        Component tasks = Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.tasks").join().getComponent();
        tasks.setClickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins tasks"));
        components.add(tasks);

        Component commands = Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.commands").join().getComponent();
        commands.setClickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins help command-page1"));
        components.add(commands);

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-").getComponent());
        return components;
    }

    @Override
    public String getId() {
        return "index";
    }
}
