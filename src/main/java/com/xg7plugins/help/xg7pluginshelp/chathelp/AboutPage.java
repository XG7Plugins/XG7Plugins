package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.help.chathelp.HelpComponent;
import com.xg7plugins.help.chathelp.HelpPage;
import com.xg7plugins.utils.text.Text;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AboutPage extends HelpPage {
    public AboutPage() {
        super("about");
    }

    private class AboutComponent extends HelpComponent {
        public AboutComponent() {
            super(null, null, null);

            addMessages(
                    new HelpComponent(
                            "&m-&6&m------------------&8*&8&m------------------&f&m-",
                            null,null
                    ),
                    new AboutComponent(),
                    HelpComponent.empty(),
                    new HelpComponent(
                            "lang:[help-in-chat.back]",
                            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "xg7plugins help about"),
                            null
                    ),
                    HelpComponent.empty(),
                    new HelpComponent(
                            "&m-&6&m------------------&8*&8&m------------------&f&m-",
                            null,null
                    )

            );
        }


        @Override
        public TextComponent build(Player player) {

            Config lang = XG7Plugins.getInstance().getLangManager() == null ?
                    XG7Plugins.getInstance().getConfig("messages") :
                    Config.of(XG7Plugins.getInstance(), XG7Plugins.getInstance().getLangManager().getLangByPlayer(XG7Plugins.getInstance(), player).join());

            String about = Text.formatComponent((String) lang.get("help-menu.about", List.class).orElse(new ArrayList<String>()).stream().collect(Collectors.joining("\n")), XG7Plugins.getInstance()).getRawText();

            return new TextComponent(
                    Text.format(about, XG7Plugins.getInstance())
                            .replace("[DISCORD]", "https://discord.gg/jfrn8w92kF")
                            .replace("[GITHUB]", "https://github.com/DaviXG7")
                            .replace("[WEBSITE]", "https://xg7plugins.com")
                            .replace("[VERSION]", XG7Plugins.getInstance().getDescription().getVersion())
                            .getWithPlaceholders(player)
            );

        }
    }
}
