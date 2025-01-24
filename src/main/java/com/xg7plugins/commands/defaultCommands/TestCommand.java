package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7menus.item.Item;

public class TestCommand implements ICommand {
    @Override
    public boolean isEnabled() {
        return ICommand.super.isEnabled();
    }

    @Override
    public Item getIcon() {
        return null;
    }
}
