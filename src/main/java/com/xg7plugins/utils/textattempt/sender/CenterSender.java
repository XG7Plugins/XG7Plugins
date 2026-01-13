package com.xg7plugins.utils.textattempt.sender;

import com.xg7plugins.utils.textattempt.Text;
import com.xg7plugins.utils.textattempt.TextCentralizer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
@Getter
public class CenterSender implements TextSender {

    private final int pixels;

    @Override
    public void send(CommandSender sender, Text text) {

        text.split("<br>").forEach(line -> {
            apply(sender, line);
            defaultSend(sender, line);
        });
    }

    @Override
    public void apply(CommandSender sender, Text text) {

        String spaces = TextCentralizer.getSpacesCentralized(pixels,text.getText());

        text.setText(spaces + text.getTextRaw());
    }
}
