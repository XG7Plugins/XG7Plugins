package com.xg7plugins.help.chathelp;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.MainCommand;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class HelpInChat {

    private final Plugin plugin;
    private final HashMap<String, HelpPage> pages = new HashMap<>();

    public HelpInChat(Plugin plugin, HelpPage index) {
        this.pages.put("index", index);
        this.plugin = plugin;

        List<ICommand> commands = plugin.getCommandManager().getCommands().values().stream().filter(cmd -> !(cmd instanceof MainCommand)).collect(Collectors.toList());

        int maxPage = (int) Math.ceil(commands.size() / 7.0);
        for (int i = 0; i < maxPage; i++) {
            pages.put("command-page" + (i + 1), new CommandInChat(commands.subList(i * 7, Math.min((i + 1) * 7, commands.size())), i, maxPage));
        }
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
                Text.format(message.getContent(), plugin).setReplacements(message.getPlaceholders()).send(sender);
            }
            return;
        }
        for (HelpComponent message : helpPage.getMessages()) {
            sender.spigot().sendMessage(message.build((Player) sender));
        }
    };




}
