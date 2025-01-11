package com.xg7plugins.help.guihelp;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7menus.menus.BaseMenu;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

public class HelpCommandGUI {

    @Getter
    private final Plugin plugin;
    private final HashMap<String, BaseMenu> menus = new HashMap<>();

    public HelpCommandGUI(Plugin plugin, BaseMenu index) {
        this.menus.put("index", index);
        this.menus.put("commands", new CommandMenu(new ArrayList<>(plugin.getCommandManager().getCommands().values()), null, null, this));
        this.plugin = plugin;
    }

    public final void registerMenu(String id, BaseMenu menu) {
        menus.put(id, menu);
    }

    public final BaseMenu getMenu(String id) {
        return menus.get(id);
    }



}
