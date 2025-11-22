package com.xg7plugins.utils.text.sender;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class TitleSender implements TextSender {

    private final int fade;
    private final int fadeIn;
    private final int fadeOut;

    @Override
    public void send(CommandSender sender, Text text) {
        if (text == null || text.getText() == null || text.getText().isEmpty()) return;

        String textString = text.getText();

        if (!(sender instanceof Player)) {
            sender.sendMessage(textString);
            return;
        }

        Player player = (Player) sender;

        String[] textSplit = textString.split(" <br> ");

        String title = textSplit[0];
        String subTitle = "";

        if (textSplit.length > 1) {
            subTitle = textSplit[1];
        }

        if (MinecraftVersion.isOlderThan(13)) {
            player.sendTitle(title, subTitle);
            return;
        }

        player.sendTitle(title, subTitle, fade, fadeOut, fadeIn);

    }

    @Override
    public void apply(CommandSender sender, Text text) {

    }
}
