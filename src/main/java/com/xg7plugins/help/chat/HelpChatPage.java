package com.xg7plugins.help.chat;

import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.Component;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface HelpChatPage {

    List<Component> getComponents(CommandSender sender);

    String getId();

    default void send(CommandSender sender) {
        List<Component> components = getComponents(sender);

        components.forEach(c -> Text.send(c, sender));
    }
}
