//package com.xg7plugins.lang;
//
//import com.sun.jna.platform.unix.solaris.LibKstat;
//import com.xg7plugins.data.config.ConfigTypeAdapter;
//import com.xg7plugins.temp.xg7menus.item.Item;
//import org.bukkit.configuration.ConfigurationSection;
//
//public class LangItemTypeAdapter implements ConfigTypeAdapter<Item> {
//
//    @Override
//    public Item fromConfig(ConfigurationSection section, Object... args) {
//
//        if (args.length != 2 && (args[0] instanceof Boolean) && (args[1] instanceof LibKstat.KstatNamed.UNION.STR)) throw new IllegalArgumentException("Invalid arguments");
//
//        boolean selected = (boolean) args[0];
//        String langid = (String) args[1];
//
//        Item langItem = Item.from(section.getString("icon"));
//
//        String name = section.getString("formated-name");
//
//        langItem.name(selected ? "§a" + name : "§7" + name);
//        langItem.lore(section.getStringList("lang-menu.item-click"));
//
//        langItem.setNBTTag("selected", selected);
//        langItem.setNBTTag("lang-id", langid);
//        return langItem;
//
//
//
//    }
//}
