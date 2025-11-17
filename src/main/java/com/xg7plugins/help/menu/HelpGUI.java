package com.xg7plugins.help.menu;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.help.HelpComponent;
import com.xg7plugins.help.menu.command.CommandGUI;
import com.xg7plugins.modules.xg7menus.menus.MenuNavigation;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

@Getter
public class HelpGUI extends MenuNavigation implements HelpComponent {

    private final Plugin plugin;

    public HelpGUI(Plugin plugin, Menu index) {
        super(new HashMap<>());
        this.menus.put("index", index);
        this.menus.put("commands", new CommandGUI(plugin, new ArrayList<>(XG7PluginsAPI.rootCommandNodesOf(plugin)), "lang:[help-menu.command-help.title]", null, this));
        this.menus.put("collaborators", new CollaboratorsMenu(this, plugin));
        this.plugin = plugin;
    }

    public final void registerMenu(String id, Menu menu) {
        menus.put(id, menu);
    }


    @Override
    public void send(CommandSender sender) {
        openMenu("index", (Player) sender);
    }
}
