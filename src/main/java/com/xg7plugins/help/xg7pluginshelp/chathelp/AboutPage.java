package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.text.Component;
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

        Config lang = XG7PluginsAPI.langManager().getLangByPlayer(XG7Plugins.getInstance(), sender instanceof Player ? ((Player) sender) : null).join().getLangConfiguration();

        String about = lang.getList("help-menu.about", String.class).orElse(new ArrayList<>()).stream().collect(Collectors.joining("\n"));

        components.add(Text.detectLangs(sender, XG7Plugins.getInstance(),about).join()
                .replace("discord", "discord.gg/jfrn8w92kF")
                .replace("github", "github.com/DaviXG7")
                .replace("website", "xg7plugins.com")
                .replace("version", XG7Plugins.getInstance().getDescription().getVersion())
        );

        components.add(Text.format(" "));

        ComponentBuilder back = ComponentBuilder.builder(
                Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.back").join()
                .replace("command", "/xg7plugins help").getText()
        );

        back.clickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins help"));

        components.add(Text.format(
                Component.text()
        ));
        components.add(Text.format(" "));

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-").getComponent());
        return components;

    }

    @Override
    public String getId() {
        return "about";
    }
}
