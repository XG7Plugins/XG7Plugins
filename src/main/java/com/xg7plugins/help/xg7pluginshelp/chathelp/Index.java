package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.help.chathelp.HelpComponent;
import com.xg7plugins.help.chathelp.HelpPage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Index extends HelpPage {
    public Index() {
        super("index");

        addMessages(
                new HelpComponent(
                        "&m-&6&m------------------&8*&8&m------------------&f&m-",
                        null,null
                ),
                new HelpComponent(
                        "lang:[help-in-chat.title]",
                        null,null
                ),
                HelpComponent.empty(),
                new HelpComponent(
                        "lang:[help-in-chat.content]",
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "xg7plugins help about"),
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Click to see about the plugins"))
                ),
                HelpComponent.empty(),
                new HelpComponent(
                        "lang:[help-in-chat.lang]",
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "xg7plugins lang"),
                        null
                ),
                new HelpComponent(
                        "lang:[help-in-chat.tasks]",
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "xg7plugins tasks"),
                        null
                ),
                new HelpComponent(
                        "lang:[help-in-chat.commands]",
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "xg7plugins help commands1"),
                        null
                ),
                new HelpComponent(
                "&m-&6&m------------------&8*&8&m------------------&f&m-",
                null,null
                )
        );
    }
}
