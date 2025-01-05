package com.xg7plugins.help;

import com.xg7plugins.libs.xg7menus.menus.BaseMenu;

import java.util.HashMap;

public abstract class HelpCommandGUI {

    private HashMap<String, BaseMenu> menus = new HashMap<>();

    public final void registerMenu(String id, BaseMenu menu) {
        menus.put(id, menu);
    }

    public BaseMenu getMenu(String id) {
        return menus.get(id);
    }



}
