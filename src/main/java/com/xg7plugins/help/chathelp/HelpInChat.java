package com.xg7plugins.help.chathelp;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Getter
public class HelpInChat {

    private final Plugin plugin;
    private final HashMap<String, HelpPage> pages = new HashMap<>();

    public HelpInChat(Plugin plugin, HelpPage index) {
        this.pages.put("index", index);
        this.plugin = plugin;
    }

    public final void registerPage(HelpPage page) {
        pages.put(page.getId(), page);
    }


    public void sendPage(String page, CommandSender sender) {
        HelpPage helpPage = pages.get(page);
        if (helpPage == null) {
            return;
        }
        if (!(sender instanceof Player)) {
            for (HelpComponent message : pages.get(page).getMessages()) {
                sender.sendMessage(Text.format(message.getContent(), plugin).getText());
            }
            return;
        }
        for (HelpComponent message : helpPage.getMessages()) {
            sender.spigot().sendMessage(message.build((Player) sender));
        }
    };




}
