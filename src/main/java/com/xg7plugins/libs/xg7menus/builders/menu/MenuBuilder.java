package com.xg7plugins.libs.xg7menus.builders.menu;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7menus.MenuException;
import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.BaseMenuBuilder;
import com.xg7plugins.libs.xg7menus.menus.gui.Menu;
import com.xg7plugins.libs.xg7menus.builders.item.SkullItemBuilder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.stream.Collectors;

public class MenuBuilder extends BaseMenuBuilder<MenuBuilder> {

    protected String title;
    protected int size;
    protected InventoryType type;

    public MenuBuilder(String id) {
        super(id);
    }

    public MenuBuilder title(String title) {
        this.title = title;
        return this;
    }
    public MenuBuilder size(int size) {
        this.size = size;
        return this;
    }
    public MenuBuilder rows(int rows) {
        this.size = rows * 9;
        return this;
    }
    public MenuBuilder type(InventoryType type) {
        this.type = type;
        return this;
    }


    @Override
    public Menu build(Player player, Plugin plugin) {
        if (title == null) throw new MenuException("The inventory must have a title!");

        Map<Integer, ItemStack> buildItems = items.entrySet().stream()
                .collect(
                        Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> {
                                BaseItemBuilder<?> builder = entry.getValue();
                                if (builder instanceof SkullItemBuilder) {
                                    SkullMeta meta = (SkullMeta) builder.toItemStack().getItemMeta();
                                    if ("THIS_PLAYER".equals(meta.getOwner())) return ((SkullItemBuilder) builder).setOwner(player.getName()).setPlaceHolders(player).toItemStack();
                                }
                                return builder.setBuildReplacements(builder.getBuildReplacements()).setPlaceHolders(player).toItemStack();
                            }

                        )
        );

        return type == null ? new Menu(id,Text.getCentralizedText(Text.PixelsSize.INV.getPixels(),Text.format(title,plugin).getWithPlaceholders(player)), size, buildItems, clickEventMap, defaultClickEvent, openMenuEvent, closeMenuEvent, allowedPermissions, player) : new Menu(id,Text.getCentralizedText(Text.PixelsSize.INV.getPixels(),Text.format(title,plugin).getWithPlaceholders(player)), type, buildItems, clickEventMap, defaultClickEvent, openMenuEvent, closeMenuEvent, allowedPermissions, player);
    }
}
