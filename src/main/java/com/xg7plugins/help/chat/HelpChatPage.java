package com.xg7plugins.help.chat;

import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface HelpChatPage {

    List<Text> getComponents(CommandSender sender);

    String getId();

    default void send(CommandSender sender) {
        List<Text> components = getComponents(sender);

        components.forEach(c -> c.send(sender));
    }
}
