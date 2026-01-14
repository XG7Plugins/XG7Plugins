package com.xg7plugins.utils.text.sender;

import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.TextCentralizer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
@Getter
public class CenterSender implements TextSender {

    private final int pixels;

    @Override
    public void send(CommandSender sender, Text text) {
        apply(sender, text);
        defaultSend(sender, text);
    }

    @Override
    public void apply(CommandSender sender, Text text) {

        String spaces = TextCentralizer.getSpacesCentralized(pixels,text.getText());

        text.setText(spaces + text.getTextRaw());
    }
}
