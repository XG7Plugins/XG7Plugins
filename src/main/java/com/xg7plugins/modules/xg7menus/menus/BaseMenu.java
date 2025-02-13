package com.xg7plugins.modules.xg7menus.menus;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7menus.MenuPermissions;
import com.xg7plugins.modules.xg7menus.events.ClickEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.item.ClickableItem;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.holders.MenuHolder;
import com.xg7plugins.modules.xg7menus.menus.holders.PageMenuHolder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter(AccessLevel.PROTECTED)
public abstract class BaseMenu {

    protected Set<MenuPermissions> menuPermissions;
    protected final Plugin plugin;
    protected final String id;


    protected BaseMenu(Plugin plugin, String id, Set<MenuPermissions> menuPermissions) {
        this.menuPermissions = menuPermissions;
        this.plugin = plugin;
        this.id = id;

        if (menuPermissions == null) {
            this.menuPermissions = new HashSet<>();
        }

    }

    protected BaseMenu(Plugin plugin, String id) {
        this(plugin, id, null);
    }

    public abstract boolean isEnabled();

    protected abstract List<Item> items(Player player);

    public void onClick(ClickEvent event) {
        event.setCancelled(true);

        items((Player) event.getWhoClicked()).stream().filter(item -> item.getSlot() == event.getClickedSlot()).findFirst().ifPresent(item -> {
            if (item instanceof ClickableItem) {
                ClickableItem clickableItem = (ClickableItem) item;
                clickableItem.getOnClick().accept(event);
            }
        });

    };

    public void onDrag(DragEvent event) {
        event.setCancelled(!menuPermissions.contains(MenuPermissions.DRAG));
    }

    public void onOpen(MenuEvent event) {
        XG7Plugins.getInstance().getDebug().info("menus", "Menu " + id + " opened by " + event.getWhoClicked().getName());
    }
    public void onClose(MenuEvent event) {
        XG7Plugins.getInstance().getDebug().info("menus", "Menu " + id + " closed by " + event.getWhoClicked().getName());

    }

    protected CompletableFuture<Void> putItems(Player player, MenuHolder holder) {
        return CompletableFuture.runAsync(() -> {
            List<Item> items = items(player);
            Bukkit.getScheduler().runTask(plugin, () -> {
                items.forEach(item -> {
                    if (item instanceof ClickableItem) {
                        ClickableItem clickItem = (ClickableItem) item;
                        holder.getUpdatedClickEvents().put(clickItem.getSlot(), clickItem.getOnClick());
                    }
                    holder.getInventory().setItem(item.getSlot(), item.getItemFor(player, plugin));
                });
            });
        }, XG7Plugins.taskManager().getAsyncExecutors().get("menus"));
    }
    public static CompletableFuture<Void> update(HumanEntity player, Item item, MenuHolder holder) {
        return CompletableFuture.runAsync(() -> {
            if (item instanceof ClickableItem) {
                ClickableItem clickableItem = (ClickableItem) item;
                holder.getUpdatedClickEvents().compute(item.getSlot(), (k,v) -> clickableItem.getOnClick());
            }
            holder.getInventory().setItem(item.getSlot(), item.getItemFor(player, holder.getPlugin()));
        }, XG7Plugins.taskManager().getAsyncExecutors().get("menus"));
    }

    public static void refresh(MenuHolder holder) {

        holder.getInventory().clear();
        holder.getUpdatedClickEvents().clear();

        holder.getMenu().putItems(holder.getPlayer(), holder).thenRun(() -> holder.getPlayer().updateInventory());
        if (holder instanceof PageMenuHolder) {
            ((PageMenuHolder) holder).goPage(0);
        }
    }

    public abstract void open(Player player);

}
