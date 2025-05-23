package com.xg7plugins.utils.text.component.sender;

import com.xg7plugins.utils.text.TextCentralizer;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.TextComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
@Getter
public class CenterSender implements TextSender {

    private final int pixels;

    @Override
    public void send(CommandSender sender, TextComponent component) {
        String spaces = TextCentralizer.getSpacesCentralized(pixels,component.getText());
        component.addFirstComponent(new Component(spaces));
        defaultSend(sender, component);
    }

    @Override
    public String serialize() {
        return "center";
    }
}
