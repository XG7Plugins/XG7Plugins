package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.help.chathelp.HelpComponent;
import com.xg7plugins.help.chathelp.HelpPage;
import com.xg7plugins.utils.text.component.event.ClickEvent;
import com.xg7plugins.utils.text.component.event.HoverEvent;
import com.xg7plugins.utils.text.component.event.action.ClickAction;
import com.xg7plugins.utils.text.component.event.action.HoverAction;
import net.md_5.bungee.api.chat.TextComponent;

public class Index extends HelpPage {
    public Index() {
        super("index");

        addMessages(
                HelpComponent.of(
                        XG7Plugins.getInstance(),
                        "&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"
                ).build(),
                HelpComponent.of(
                        XG7Plugins.getInstance(),
                        "lang:[help-in-chat.title]"
                ).build(),
                HelpComponent.empty(),
                HelpComponent.of(
                        XG7Plugins.getInstance(),
                        "lang:[help-in-chat.content]"
                )
                        .clickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins help about"))
                        .hoverEvent(HoverEvent.of(HoverAction.SHOW_TEXT, "Click to see about the plugins"))
                        .build(),
                HelpComponent.empty(),
                HelpComponent.of(
                        XG7Plugins.getInstance(),
                        "lang:[help-in-chat.lang]"
                ).clickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins lang")).build(),
                HelpComponent.of(
                        XG7Plugins.getInstance(),
                        "lang:[help-in-chat.tasks]"
                ).clickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins tasks")).build(),
                HelpComponent.of(
                        XG7Plugins.getInstance(),
                        "lang:[help-in-chat.commands]"
                ).clickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins help command-page1")).build(),
                HelpComponent.of(
                        XG7Plugins.getInstance(),
                        "&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"
                ).build()
        );
    }
}
