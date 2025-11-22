package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.ClickEvent;
import com.xg7plugins.utils.text.component.TextComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AboutPage implements HelpChatPage {

    @Override
    public List<Text> getComponents(CommandSender sender) {

        List<Text> components = new ArrayList<>();

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));

        ConfigSection lang = XG7Plugins.getAPI().langManager().getLangByPlayer(XG7Plugins.getInstance(), sender instanceof Player ? ((Player) sender) : null).join().getSecond().getLangConfiguration();

        String about = lang.getList("help-menu.about", String.class).orElse(new ArrayList<>()).stream().collect(Collectors.joining("\n"));

        components.add(Text.detectLangs(sender, XG7Plugins.getInstance(),about).join()
                .replace("discord", "discord.gg/jfrn8w92kF")
                .replace("github", "github.com/DaviXG7")
                .replace("website", "xg7plugins.com")
                .replace("version", XG7Plugins.getInstance().getVersion())
        );

        components.add(Text.format(" "));


        components.add(
                TextComponentBuilder.of(
                        Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.back").join()
                                .replace("command", "/xg7plugins help").getText()
                ).clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7plugins help"))
                        .build()
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
