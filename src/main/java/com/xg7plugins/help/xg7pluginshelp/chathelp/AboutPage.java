package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.help.chathelp.HelpComponent;
import com.xg7plugins.help.chathelp.HelpPage;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.event.ClickEvent;
import com.xg7plugins.utils.text.component.event.action.ClickAction;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AboutPage extends HelpPage {
    public AboutPage() {
        super("about");
        addMessages(
                HelpComponent.of(
                        XG7Plugins.getInstance(),
                        "&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"
                ).build(),
                new AboutComponent(),
                HelpComponent.empty(),
                HelpComponent.of(
                        XG7Plugins.getInstance(),
                        "lang:[help-in-chat.back]"
                ).clickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins help")).build(),
                HelpComponent.empty(),
                HelpComponent.of(
                        XG7Plugins.getInstance(),
                        "&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"
                ).build()

        );
    }

    private class AboutComponent extends HelpComponent {
        public AboutComponent() {
            super(XG7Plugins.getInstance(),null,null, null);
        }


        @Override
        public Component buildFor(Player player) {

            Config lang = XG7Plugins.getInstance().getLangManager().getLangByPlayer(XG7Plugins.getInstance(), player).join().getLangConfiguration();

            String about = lang.getList("help-menu.about", String.class).orElse(new ArrayList<>()).stream().collect(Collectors.joining("\n"));

            return Component.text(
                    Text.detectLangs(player, XG7Plugins.getInstance(),about).join()
                    .replace("discord", "discord.gg/jfrn8w92kF")
                    .replace("github", "github.com/DaviXG7")
                    .replace("website", "xg7plugins.com")
                    .replace("version", XG7Plugins.getInstance().getDescription().getVersion())
                    .getText()
            ).build();

        }

    }
}
