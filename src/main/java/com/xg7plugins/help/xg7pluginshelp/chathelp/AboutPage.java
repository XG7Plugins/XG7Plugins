package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.ComponentBuilder;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.TextComponent;
import com.xg7plugins.utils.text.component.events.ClickEvent;
import com.xg7plugins.utils.text.component.events.action.ClickAction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AboutPage implements HelpChatPage {

    @Override
    public List<TextComponent> getComponents(CommandSender sender) {

        List<TextComponent> components = new ArrayList<>();

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-").getComponent());

        Config lang = XG7PluginsAPI.langManager().getLangByPlayer(XG7Plugins.getInstance(), sender instanceof Player ? ((Player) sender) : null).join().getLangConfiguration();

        String about = lang.getList("help-menu.about", String.class).orElse(new ArrayList<>()).stream().collect(Collectors.joining("\n"));

        components.add(new TextComponent(Text.detectLangs(sender, XG7Plugins.getInstance(),about).join()
                .replace("discord", "discord.gg/jfrn8w92kF")
                .replace("github", "github.com/DaviXG7")
                .replace("website", "xg7plugins.com")
                .replace("version", XG7Plugins.getInstance().getDescription().getVersion())
                .getText()));

        components.add(TextComponent.empty());

        ComponentBuilder back = ComponentBuilder.builder(
                Text.fromLang(sender, XG7Plugins.getInstance(), "help-in-chat.back").join()
                .replace("command", "/xg7plugins help").getText()
        );

        back.clickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/xg7plugins help"));

        components.add(back.buildTextComponent());
        components.add(TextComponent.empty());

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-").getComponent());
        return components;

    }

    @Override
    public String getId() {
        return "about";
    }
}
