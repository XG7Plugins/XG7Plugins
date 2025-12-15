package com.xg7plugins.menus.config;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.editor.InGameEditor;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.ChangePageItem;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import com.xg7plugins.modules.xg7menus.utils.ConfirmationMenu;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class ConfigIndexMenu extends PagedMenu {

    private final InGameEditor editor;

    private final ConfirmationMenu closeConfirmation;

    public ConfigIndexMenu(InGameEditor editor) {
        super(MenuConfigurations.of(
                XG7Plugins.getInstance(),
                "config-editor",
                "lang:[config-editor.title]",
                6,
                EnumSet.noneOf(MenuAction.class),
                true,
                Collections.singletonList(Pair.of("path", editor.getCurrentSection().getPath().isEmpty() ? "root" : editor.getCurrentSection().getPath())),
                1000L
        ), Slot.of(2, 2), Slot.of(4, 8));

        this.editor = editor;
        this.closeConfirmation = ConfirmationMenu.Builder.create(getMenuConfigs().getPlugin(), "config-editor-confirm")
                .title("lang:[config-editor.confirm.close-title]")
                .yesItem(Item.from(XMaterial.LIME_WOOL).name("lang:[config-editor.confirm.confirm]"))
                .noItem(Item.from(XMaterial.RED_WOOL).name("lang:[config-editor.confirm.back]"))
                .middleItem(Item.from(XMaterial.PAPER).name("lang:[config-editor.confirm.sure-to-close]"))
                .onYesClick(actionEvent -> {
                    actionEvent.getHolder().getPlayer().closeInventory();
                    try {
                        editor.getCurrentSection().getFile().reload();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onNoClick(actionEvent -> {
                    Player player = actionEvent.getHolder().getPlayer();
                    player.closeInventory();
                    open(player);
                })
                .build();
    }

    @Override
    public List<InventoryItem> getItems(Player player) {

        return Arrays.asList(
                ChangePageItem.previousPageItem(Slot.of(6, 1))
                        .name("lang:[go-back-item]"),

                Item.from(XMaterial.IRON_AXE)
                        .name("lang:[config-editor.back-to-previous-section]")
                        .toClickableInventoryItem(Slot.of(6, 4), (actionEvent -> {
                            player.closeInventory();
                            editor.sendPage(editor.getCurrentSection().parent());
                        })),

                Item.from(XMaterial.PAPER)
                        .name("lang:[config-editor.save]")
                        .toClickableInventoryItem(Slot.of(6, 6), (actionEvent -> editor.save())),

                Item.from(XMaterial.CHEST)
                        .name("lang:[config-editor.add.item]")
                        .toClickableInventoryItem(Slot.of(6, 8), (actionEvent -> {
                            player.closeInventory();
                            editor.sendAddRequest();
                        })),

                ChangePageItem.nextPageItem(Slot.of(6, 9))
                        .name("lang:[go-next-item]")
        );
    }

    @Override
    public List<InventoryItem> pagedItems(Player player) {

        List<InventoryItem> items = new ArrayList<>();

        ConfigSection section = editor.getCurrentSection();

        for (String key : section.getKeys(false)) {

            if (key.equals("config-version")) continue;

            Object object = section.get(key);

            Class<?> type = section.getType(key);

            items.add(
                    Item.from(Material.EMERALD)
                            .name(key)
                            .lore(
                                    "lang:[config-editor.key.value]",
                                    "lang:[config-editor.key.type]",
                                    object instanceof Map ? "lang:[config-editor.key.click-to-see]"
                                            : object instanceof Boolean ?
                                            "lang:[config-editor.key.click-to-toggle]"
                                            : "lang:[config-editor.key.click-to-edit]",
                                    "lang:[config-editor.key.click-to-delete]"
                            )
                            .setBuildPlaceholders(Arrays.asList(
                                    Pair.of("value", object.toString()),
                                    Pair.of("type", type.getSimpleName())
                            ))
                            .toClickableInventoryItem(Slot.fromSlot(-1, true), actionEvent -> {
                                player.closeInventory();

                                if (actionEvent.getMenuAction().isLeftClick()) {
                                    if (object instanceof Map) {
                                        editor.sendPage(editor.getCurrentSection().child(key));
                                        return;
                                    }
                                    editor.sendEditRequest(key, type);
                                    return;
                                }

                                ConfirmationMenu.Builder.create(getMenuConfigs().getPlugin(), "config-editor-confirm-delete")
                                        .title("lang:[config-editor.confirm-delete.title]")
                                        .yesItem(Item.from(XMaterial.LIME_WOOL).name("lang:[config-editor.confirm-delete.confirm]"))
                                        .noItem(Item.from(XMaterial.RED_WOOL).name("lang:[config-editor.confirm-delete.back]"))
                                        .middleItem(Item.from(XMaterial.PAPER).name("lang:[config-editor.confirm-delete.sure-to-delete]"))
                                        .onYesClick(actionEvent1 -> {
                                            editor.removeKey(key);
                                            editor.sendPage(editor.getCurrentSection());
                                        })
                                        .onNoClick(actionEvent1 -> editor.sendPage(editor.getCurrentSection()))
                                        .build()
                                        .open(player);
                            })
            );
        }

        return items;
    }

    @Override
    public void onRepeatingUpdate(BasicMenuHolder holder) {
        holder.getInventoryUpdater().setItem(Item.from(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR))
                .name("lang:[close-item]")
                .lore("lang:[config-editor.config-saved]")
                .setBuildPlaceholders(Collections.singletonList(Pair.of("saved", editor.isSaved() + "")))
                .toClickableInventoryItem(Slot.of(6, 5), (actionEvent -> {
                    if (editor.isSaved()) {
                        actionEvent.getHolder().getPlayer().closeInventory();
                        return;
                    }
                    closeConfirmation.open(actionEvent.getHolder().getPlayer());
                })));
    }
}
