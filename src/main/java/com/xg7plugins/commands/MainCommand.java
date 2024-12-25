package com.xg7plugins.commands;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7menus.item.Item;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MainCommand implements ICommand {

    private final Plugin plugin;


    @Override
    public Item getIcon() {
        return null;
    }
}
