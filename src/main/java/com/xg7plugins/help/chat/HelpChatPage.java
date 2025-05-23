package com.xg7plugins.help.chat;

import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface HelpChatPage {

    List<TextComponent> getComponents(CommandSender sender);

    String getId();

    default void send(CommandSender sender) {
        List<TextComponent> components = getComponents(sender);

        components.forEach(c -> c.send(sender));
    }
}
