package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.ComponentBuilder;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.TextComponent;
import com.xg7plugins.utils.text.component.events.ClickEvent;
import com.xg7plugins.utils.text.component.events.HoverEvent;
import com.xg7plugins.utils.text.component.events.action.ClickAction;
import com.xg7plugins.utils.text.component.events.action.HoverAction;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Index implements HelpChatPage {

    @Override
    public List<TextComponent> getComponents(CommandSender sender) {

        List<TextComponent> components = new ArrayList<>();

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-").getComponent());

        components.add(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.title").join()
                .getComponent());

        components.add(TextComponent.empty());

        ComponentBuilder content = ComponentBuilder.builder(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.content").join().getText());
        content.clickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins help about"));
        content.hoverEvent(HoverEvent.of(HoverAction.SHOW_TEXT, "Click to see about the plugins"));
        components.add(content.buildTextComponent());

        components.add(TextComponent.empty());

        ComponentBuilder lang = ComponentBuilder.builder(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.lang").join().getText());
        lang.clickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins lang"));
        components.add(lang.buildTextComponent());

        ComponentBuilder tasks = ComponentBuilder.builder(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.tasks").join().getText());
        tasks.clickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins tasks"));
        components.add(tasks.buildTextComponent());

        ComponentBuilder commands = ComponentBuilder.builder(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.commands").join().getText());
        commands.clickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins help command-page1"));
        components.add(commands.buildTextComponent());

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-").getComponent());
        return components;
    }

    @Override
    public String getId() {
        return "index";
    }
}
