package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AboutPage implements HelpChatPage {

    @Override
    public List<Text> getComponents(CommandSender sender) {

        List<Text> components = new ArrayList<>();

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));

        components.add(Text.fromLang(sender, XG7Plugins.getInstance(), "help-menu.about")
                .replace("discord", "discord.gg/jfrn8w92kF")
                .replace("github", "github.com/DaviXG7")
                .replace("website", "xg7plugins.com")
                .replace("version", XG7Plugins.getInstance().getVersion())
                .replaceLiteral("<endp>", "<br>")
        );

        components.add(Text.format(" "));


        components.add(
                Text.format(Component.text(Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.back")
                                .replace("command", "/xg7plugins help").getText())
                                .clickEvent(ClickEvent.suggestCommand( "/xg7plugins help"))
                )

        );
        components.add(Text.format(" "));

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));
        return components;

    }

    @Override
    public String getId() {
        return "about";
    }
}
