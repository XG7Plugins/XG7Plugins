package com.xg7plugins.help.guihelp;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7menus.menus.BaseMenu;
import com.xg7plugins.libs.xg7menus.menus.MenuNavigation;
import com.xg7plugins.libs.xg7menus.menus.gui.Menu;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

public class HelpCommandGUI extends MenuNavigation {

    @Getter
    private final Plugin plugin;

    public HelpCommandGUI(Plugin plugin, Menu index) {
        super(new HashMap<>());
        this.menus.put("index", index);
        this.menus.put("commands", new CommandMenu(new ArrayList<>(plugin.getCommandManager().getCommands().values()), null, null, this));
        this.plugin = plugin;
    }

    public final void registerMenu(String id, Menu menu) {
        menus.put(id, menu);
    }



}
