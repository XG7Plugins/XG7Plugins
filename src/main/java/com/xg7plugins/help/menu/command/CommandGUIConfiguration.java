package com.xg7plugins.help.menu.command;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.utils.Pair;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class CommandGUIConfiguration implements MenuConfigurations {

    private final String commandName;
    private final Plugin plugin;
    private final String title;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getId() {
        return "command_menu-" + UUID.randomUUID();
    }

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public List<Pair<String,String>> getPlaceholders() {
        return Arrays.asList(
                Pair.of("subcommand", commandName == null ? "" : commandName),
                Pair.of("plugin_name", plugin.getName())
        );
    }

}
