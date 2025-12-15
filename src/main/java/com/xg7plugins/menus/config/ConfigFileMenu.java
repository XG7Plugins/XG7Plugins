package com.xg7plugins.menus.config;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.editor.impl.DialogEditor;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ConfigFileMenu extends PagedMenu {
    public ConfigFileMenu(Plugin plugin) {
        super(MenuConfigurations.of(
                plugin,
                "id",
                "Configs",
                4,
                true
        ), Slot.of(2,2), Slot.of(2, 8));
    }

    @Override
    public List<InventoryItem> pagedItems(Player player) {

        List<File> yamls = new ArrayList<>();

        try {

            Files.walk(Paths.get(getMenuConfigs().getPlugin().getJavaPlugin().getDataFolder().toURI()))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                    .map(Path::toFile)
                    .forEach(yamls::add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<InventoryItem> items = new ArrayList<>();

        yamls.forEach(yaml -> {
            items.add(Item.from(Material.PAPER)
                    .name(yaml.getName())
                    .toClickableInventoryItem(1, event -> {

                        new DialogEditor(player).sendPage(ConfigFile.of());
                    })
            );
        });




        return List.of();
    }

    @Override
    public List<InventoryItem> getItems(Player player) {
        return List.of();
    }
}
