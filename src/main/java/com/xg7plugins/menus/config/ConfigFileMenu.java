package com.xg7plugins.menus.config;

import com.cryptomorin.xseries.XMaterial;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.editor.impl.ConversationEditor;
import com.xg7plugins.config.editor.impl.DialogEditor;
import com.xg7plugins.config.editor.impl.FormEditor;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.ChangePageItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.CloseInventoryItem;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ConfigFileMenu extends PagedMenu {

    private final BiConsumer<Player, File> configFileConsumer;

    private final Plugin filesPlugin;

    public ConfigFileMenu(Plugin plugin) {
        super(MenuConfigurations.of(
                XG7Plugins.getInstance(),
                "id",
                "lang:[config-files.title]",
                3,
                EnumSet.noneOf(MenuAction.class),
                true,
                Collections.singletonList(Pair.of("plugin", plugin.getName()))
        ), Slot.of(2,2), Slot.of(2, 8));

        this.filesPlugin = plugin;

        this.configFileConsumer = (player, yaml) -> {
            player.closeInventory();
            try {
                ConfigFile configFile = new ConfigFile(filesPlugin,  yaml);

                ConfigSection mainConfig = ConfigFile.mainConfigOf(XG7Plugins.getInstance()).section("config-editor");

                boolean enabled = XG7Plugins.getAPI().isGeyserFormsEnabled();

                if (enabled && mainConfig.get("form-editor", false)) {
                    if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                        new FormEditor(player).sendPage(configFile.root());
                        return;
                    }
                }

                if (mainConfig.get("dialog-editor", true) && MinecraftServerVersion.isNewerOrEqual(ServerVersion.V_1_21_6)) {
                    new DialogEditor(player).sendPage(configFile.root());
                    return;
                }

                new ConversationEditor(player).sendPage(configFile.root());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public List<InventoryItem> pagedItems(Player player) {

        List<File> yamls = new ArrayList<>();

        try (Stream<Path> files = Files.walk(Paths.get(filesPlugin.getJavaPlugin().getDataFolder().toURI()))) {
            files.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                    .map(Path::toFile)
                    .forEach(yamls::add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<InventoryItem> items = new ArrayList<>();

        yamls.forEach(yaml -> items.add(Item.from(Material.BOOK)
                .name("lang:[config-files.config-item.name]")
                .lore("lang:[config-files.config-item.lore]")
                .setBuildPlaceholders(Arrays.asList(
                        Pair.of("config_name", yaml.getName()),
                        Pair.of("config_path", yaml.getAbsolutePath().replace(
                                filesPlugin.getJavaPlugin().getDataFolder().getParentFile().getAbsolutePath() + "\\",
                                ""
                            ).replace("\\", "/")
                        )
                ))
                .toClickableInventoryItem(1, event -> configFileConsumer.accept(player, yaml))
        ));

        return items;
    }

    @Override
    public List<InventoryItem> getItems(Player player) {
        return Arrays.asList(
                CloseInventoryItem.get(Slot.of(3, 1)).name("lang:[close-item]"),
                ChangePageItem.previousPageItem(Slot.of(3, 8)).name("lang:[go-back-item]"),
                ChangePageItem.nextPageItem(Slot.of(3, 9)).name("lang:[go-next-item]")
        );
    }
}
