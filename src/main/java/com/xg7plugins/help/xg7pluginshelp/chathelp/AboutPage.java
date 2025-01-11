package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.help.chathelp.HelpComponent;
import com.xg7plugins.help.chathelp.HelpPage;
import com.xg7plugins.utils.text.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AboutPage extends HelpPage {
    public AboutPage() {
        super("about");
        addMessages(
                new HelpComponent(
                        "&m-&9&m-&6&m------------------&e*&6&m-----------------&9&m--&f&m-",
                        null,null
                ),
                new AboutComponent(),
                HelpComponent.empty(),
                new HelpComponent(
                        "lang:[help-in-chat.back]",
                        new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "xg7plugins help"),
                        null
                ),
                HelpComponent.empty(),
                new HelpComponent(
                        "&m-&9&m-&6&m------------------&e*&6&m-----------------&9&m--&f&m-",
                        null,null
                )

        );
    }

    private class AboutComponent extends HelpComponent {
        public AboutComponent() {
            super(null, null, null);
        }


        @Override
        public TextComponent build(Player player) {

            Config lang = XG7Plugins.getInstance().getLangManager() == null ?
                    XG7Plugins.getInstance().getConfig("messages") :
                    Config.of(XG7Plugins.getInstance(), XG7Plugins.getInstance().getLangManager().getLangByPlayer(XG7Plugins.getInstance(), player).join());

            String about = (String) lang.get("help-menu.about", List.class).orElse(new ArrayList<String>()).stream().collect(Collectors.joining("\n"));

            return new TextComponent(
                    Text.detectLangOrText(XG7Plugins.getInstance(),player,about).join()
                    .replace("[DISCORD]", "discord.gg/jfrn8w92kF")
                    .replace("[GITHUB]", "github.com/DaviXG7")
                    .replace("[WEBSITE]", "xg7plugins.com")
                    .replace("[VERSION]", XG7Plugins.getInstance().getDescription().getVersion())
                    .getText()
            );

        }
    }
}
