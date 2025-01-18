package com.xg7plugins.commands.defaultCommands.reloadCommand;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.CommandArgs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
@Getter
public abstract class ReloadExpansion {

    private final String name;
    private final Plugin plugin;

    private final String permission;

    public abstract void onReload(CommandSender sender, CommandArgs args);
}
