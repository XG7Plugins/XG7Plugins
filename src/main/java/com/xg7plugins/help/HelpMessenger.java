package com.xg7plugins.help;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.core.MainConfigSection;
import com.xg7plugins.help.chat.HelpChat;
import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.help.form.HelpForm;
import com.xg7plugins.help.menu.HelpGUI;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

@AllArgsConstructor
@Getter
public class HelpMessenger {

    private final Plugin plugin;

    private final HelpGUI gui;
    private final HelpForm form;
    private final HelpChat chat;

    public void send(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendChat(sender);
            return;
        }

        Player player = (Player) sender;

        MainConfigSection config = Config.of(XG7Plugins.getInstance(), MainConfigSection.class);

        if (XG7PluginsAPI.isGeyserFormsEnabled() && config.isHelpCommandForm() && FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                sendForm(player);
                return;
        }

        if (config.isHelpCommandInGui()) {
            sendGUI(player);
            return;
        }

        sendChat(player);
    }

    public void sendChat(CommandSender sender) {
        chat.send(sender);
    }
    public void sendChat(CommandSender sender, String page) {
        HelpChatPage helpChatPage = chat.getPage(page);
        if (helpChatPage == null) {
            Text.sendTextFromLang(sender, plugin, "help-in-chat.page-not-found");
            return;
        }
        helpChatPage.send(sender);
    }
    public void sendGUI(CommandSender sender) {
        gui.send(sender);
    }
    public void sendForm(CommandSender sender) {
        form.send(sender);
    }


}
