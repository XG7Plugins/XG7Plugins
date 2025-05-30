package com.xg7plugins.help.chat;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.executors.MainCommand;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.help.HelpComponent;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class HelpChat implements HelpComponent {

    private final HashMap<String, HelpChatPage> pages = new HashMap<>();

    public HelpChat(Plugin plugin, HelpChatPage index) {
        this.pages.put("index", index);

        List<Command> commands = XG7PluginsAPI.commandListOf(plugin).stream().filter(cmd -> !(cmd instanceof MainCommand)).collect(Collectors.toList());

        int maxPage = (int) Math.ceil(commands.size() / 7.0);
        for (int i = 0; i < maxPage; i++) {
            pages.put("command-page" + (i + 1), new CommandHelp(plugin, commands.subList(i * 6, Math.min((i + 1) * 6, commands.size())), i, maxPage));
        }
    }

    public void registerPage(HelpChatPage page) {
        pages.put(page.getId(), page);
    }
    public HelpChatPage getPage(String id) {
        return pages.get(id);
    }


    @Override
    public void send(CommandSender sender) {
        pages.get("index").send(sender);
    }
}
