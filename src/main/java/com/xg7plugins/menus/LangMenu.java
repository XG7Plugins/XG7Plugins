//package com.xg7plugins.menus;
//
//import com.xg7plugins.XG7Plugins;
//import com.xg7plugins.data.config.Config;
//import com.xg7plugins.data.playerdata.PlayerData;
//import com.xg7plugins.data.playerdata.PlayerDataDAO;
//import com.xg7plugins.temp.xg7menus.Slot;
//import com.cryptomorin.xseries.XMaterial;
//import com.xg7plugins.temp.xg7menus.events.ClickEvent;
//import com.xg7plugins.temp.xg7menus.events.MenuEvent;
//import com.xg7plugins.temp.xg7menus.item.Item;
//import com.xg7plugins.temp.xg7menus.menus.gui.PageMenu;
//import com.xg7plugins.temp.xg7menus.menus.holders.PageMenuHolder;
//import com.xg7plugins.utils.text.Text;
//import org.bukkit.entity.Player;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class LangMenu extends PageMenu {
//    public LangMenu(XG7Plugins plugin) {
//        super(plugin, "lang-menu", "lang:[lang-menu.title]", 54, Slot.of(2,2), Slot.of(5,8));
//    }
//
//    @Override
//    public List<Item> pagedItems(Player player) {
//
//        XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();
//
//        PlayerData language = XG7Plugins.getInstance().getPlayerDataDAO().get(player.getUniqueId()).join();
//
//        List<Item> pagedItems = new ArrayList<>();
//
//        XG7Plugins.getInstance().getLangManager().getLangs().asMap().join().entrySet().stream().filter(entry -> entry.getKey().contains("XG7Plugins")).forEach((map)-> {
//            boolean selected = language != null && language.getLangId().equals(map.getKey().contains(":") ? map.getKey().split(":")[1] : map.getKey());
//
//            pagedItems.add(Config.of(XG7Plugins.getInstance(), map.getValue()).get("", Item.class, selected, map.getKey()).orElse(null));
//        });
//
//        return pagedItems;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return XG7Plugins.getInstance().getConfig("config").get("enable-langs", Boolean.class).orElse(false);
//    }
//
//    @Override
//    protected List<Item> items(Player player) {
//        return Arrays.asList(
//                Item.from(XMaterial.ARROW).name("lang:[go-back-item]").slot(45),
//                Item.from(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR)).name("lang:[close-item]").slot(49),
//                Item.from(XMaterial.ARROW).name("lang:[go-next-item]").slot(53)
//        );
//    }
//
//    @Override
//    public void onClick(MenuEvent event) {
//        event.setCancelled(true);
//        if (!(event instanceof ClickEvent)) return;
//        ClickEvent clickEvent = (ClickEvent) event;
//        Player player = (Player) clickEvent.getWhoClicked();
//
//        PageMenuHolder holder = (PageMenuHolder) clickEvent.getInventoryHolder();
//
//        switch (clickEvent.getClickedSlot()) {
//            case 45:
//                holder.previousPage();
//                break;
//            case 49:
//                player.closeInventory();
//                break;
//            case 53:
//                holder.nextPage();
//                break;
//            default:
//                if (clickEvent.getClickedItem() == null || clickEvent.getClickedItem().isAir()) return;
//
//                String langName = clickEvent.getClickedItem().getTag("lang-id", String.class).orElse(null);
//                boolean selected = clickEvent.getClickedItem().getTag("selected", Boolean.class).orElse(false);
//
//                if (selected) {
//                    Text.formatLang(plugin, player, "lang-menu.already-selected").thenAccept(text -> text.toComponent().send(player));
//                    return;
//                }
//
//                if (XG7Plugins.getInstance().getCooldownManager().containsPlayer("lang-change", player)) {
//
//                    double cooldownToToggle = XG7Plugins.getInstance().getCooldownManager().getReamingTime("lang-change", player);
//
//                    Text.formatLang(plugin,player, "lang-menu.cooldown-to-toggle").thenAccept(
//                            text -> text.replace("[MILLISECONDS]", String.valueOf((cooldownToToggle)))
//                                    .replace("[SECONDS]", String.valueOf((int) ((cooldownToToggle) / 1000)))
//                                    .replace("[MINUTES]", String.valueOf((int) ((cooldownToToggle) / 60000)))
//                                    .replace("[HOURS]", String.valueOf((int) ((cooldownToToggle) / 3600000)))
//                                    .send(player)
//                    );
//
//                    return;
//                }
//
//                PlayerDataDAO dao = XG7Plugins.getInstance().getPlayerDataDAO();
//
//                String dbLang = langName.split(":")[1];
//
//                dao.update(new PlayerData(player.getUniqueId(), dbLang)).thenAccept(r -> {
//                    XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();
//                    Text.formatLang(plugin, player, "lang-menu.toggle-success").thenAccept(text -> text.send(player));
//                    player.closeInventory();
//                    open(player);
//                    refresh(holder);
//                });
//
//                XG7Plugins.getInstance().getCooldownManager().addCooldown(player, "lang-change", XG7Plugins.getInstance().getConfig("config").getTime("cooldown-to-toggle-lang").orElse(5000L));
//
//        }
//    }
//}
