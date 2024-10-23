package com.xg7plugins.libs.xg7menus.builders.item;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.commands.setup.ISubCommand;
import com.xg7plugins.commands.setup.SubCommand;
import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.menus.CommandMenu;
import com.xg7plugins.utils.reflection.ReflectionClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {

    public ItemBuilder(ItemStack stack, Plugin plugin) {
        super(stack,plugin);
    }

    @Contract("_,_ -> new")
    public static @NotNull ItemBuilder from(Material material,Plugin plugin) {
        return new ItemBuilder(new ItemStack(material),plugin);
    }
    @Contract("_,_ -> new")
    public static @NotNull ItemBuilder from(@NotNull MaterialData material,Plugin plugin) {
        return new ItemBuilder(material.toItemStack(),plugin);
    }
    @Contract("_,_ -> new")
    public static @NotNull ItemBuilder from(ItemStack itemStack,Plugin plugin) {
        return new ItemBuilder(itemStack,plugin);
    }

    @Contract("_,_,_ -> new")
    public static @NotNull ItemBuilder commandIcon(XMaterial material, ICommand command, Plugin plugin) {

        Command setup = ReflectionClass.of(command.getClass()).getAnnotation(Command.class);

        ItemBuilder builder = new ItemBuilder(material.parseItem(),plugin);

        List<String> lore = new ArrayList<>();

        lore.add("lang:[commands-menu.command-item.usage]");
        lore.add("lang:[commands-menu.command-item.desc]");
        lore.add("lang:[commands-menu.command-item.perm]");
        lore.add("lang:[commands-menu.command-item.player-only]");
        if (command.getSubCommands().length != 0) {
            lore.add("lang:[commands-menu.if-subcommand]");
            builder.click(clickEvent -> CommandMenu.createSubCommandMenu(plugin, clickEvent.getWhoClicked(), command));
        }

        builder.name(setup.name());
        builder.lore(lore);
        builder.setBuildReplacements(new HashMap<String, String>() {{
            put("[SYNTAX]", setup.syntax());
            put("[DESCRIPTION]", setup.description());
            put("[PERMISSION]", setup.perm());
            put("[PLAYER_ONLY]", setup.isOnlyPlayer() + "");
        }});

        return builder;
    }
    @Contract("_,_,_ -> new")
    public static @NotNull ItemBuilder subCommandIcon(XMaterial material, ISubCommand command, Plugin plugin) {

        SubCommand setup = ReflectionClass.of(command.getClass()).getAnnotation(SubCommand.class);

        ItemBuilder builder = new ItemBuilder(material.parseItem(),plugin);

        List<String> lore = new ArrayList<>();

        lore.add("lang:[subcommands-menu.command-item.usage]");
        lore.add("lang:[subcommands-menu.command-item.desc]");
        lore.add("lang:[subcommands-menu.command-item.perm]");
        lore.add("lang:[subcommands-menu.command-item.player-only]");
        lore.add("lang:[subcommands-menu.command-item.type]");
        if (command.getSubCommands().length != 0) {
            lore.add("lang:[subcommands-menu.if-subcommand]");
            builder.click(clickEvent -> CommandMenu.createSubCommandMenu(plugin, clickEvent.getWhoClicked(), command));

        }

        builder.name(setup.name());
        builder.lore(lore);
        builder.setBuildReplacements(new HashMap<String, String>() {{
            put("[SYNTAX]", setup.syntax());
            put("[DESCRIPTION]", setup.description());
            put("[PERMISSION]", setup.perm());
            put("[PLAYER_ONLY]", setup.isOnlyPlayer() + "");
            put("[TYPE]", setup.type() + "");
        }});

        return builder;
    }


}
