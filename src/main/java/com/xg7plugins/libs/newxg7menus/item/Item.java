package com.xg7plugins.libs.newxg7menus.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.newxg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class Item {

    protected ItemStack itemStack;
    private Consumer<ClickEvent> onClick;
    protected int slot;

    protected HashMap<String, String> buildPlaceholders = new HashMap<>();


    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.slot = -1;
    }

    public static Item from(ItemStack itemStack) {
        return new Item(itemStack);
    }
    public static Item from(XMaterial material) {
        return new Item(material.parseItem());
    }
    public static Item from(Material material) {
        return new Item(new ItemStack(material));
    }
    public static Item from(Material material, int amount) {
        return new Item(new ItemStack(material, amount));
    }
    public static Item from(XMaterial material, int amount) {
        return new Item(material.parseItem(amount));
    }
    public static Item from(MaterialData data) {
        return new Item(data.toItemStack());
    }
    public static Item from(MaterialData data, int amount) {
        return new Item(data.toItemStack(amount));
    }

    public Item slot(int slot) {
        this.slot = slot;
        return this;
    }

    public Item onClick(Consumer<MenuEvent> onClick) {
        this.onClick = onClick;
        return this;
    }

    public Item setBuildPlaceholders(HashMap<String, String> buildPlaceholders) {
        this.buildPlaceholders = buildPlaceholders;
        return this;
    }

    public Item amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public Item data(MaterialData data) {
        this.itemStack.setData(data);
        return this;
    }
    public Item meta(ItemMeta meta) {
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item name(String name) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(name);
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item lore(String... lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(Arrays.stream(lore).collect(Collectors.toList()));
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item lore(String lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item lore(List<String> lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(lore);
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item enchants(Map<Enchantment, Integer> enchants) {
        this.itemStack.addUnsafeEnchantments(enchants);
        return this;
    }
    public Item enchant(Enchantment enchant, int level) {
        this.itemStack.addUnsafeEnchantment(enchant, level);
        return this;
    }
    public Item flags(ItemFlag... flags) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addItemFlags(flags);
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item customModelData(int data) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setCustomModelData(data);
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item unbreakable(boolean unbreakable) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item setNBTTag(String key, Object value) {
        Gson gson = new Gson();

        ReflectionClass nbtTagCompoundClass = NMSUtil.getNMSClass("NBTTagCompound");
        ReflectionClass nmsItemStackClass = NMSUtil.getNMSClass("ItemStack");
        ReflectionClass craftItemStackClass = NMSUtil.getCraftBukkitClass("inventory.CraftItemStack");

        ReflectionObject nmsItem = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invokeToRObject(itemStack);
        ReflectionObject tag = nmsItemStackClass.getMethod("getTag").invokeToRObject(nmsItem);

        if (tag.getObject() == null) tag = nbtTagCompoundClass.newInstance();

        String jsonValue = gson.toJson(value);

        tag.getMethod("setTag", String.class, String.class).invoke(key, jsonValue);

        nmsItem.getMethod("setTag", nbtTagCompoundClass.getAClass()).invoke(tag.getObject());

        this.itemStack = craftItemStackClass.getMethod("asBukkitCopy", nmsItemStackClass.getAClass()).invoke(nmsItem);
        return this;
    }
    public static <T> T getTag(String key, ItemStack item, Class<T> clazz) {
        Gson gson = new Gson();

        ReflectionClass nmsItemStackClass = NMSUtil.getNMSClass("ItemStack");
        ReflectionClass craftItemStackClass = NMSUtil.getCraftBukkitClass("inventory.CraftItemStack");

        ReflectionObject nmsItem = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invokeToRObject(item);
        ReflectionObject tag = nmsItemStackClass.getMethod("getTag").invokeToRObject(nmsItem);

        if (tag != null) {
            String jsonValue = tag.getMethod("getString", String.class).invoke(key);
            return gson.fromJson(jsonValue, clazz);
        }
        return null;
    }

    @SneakyThrows
    public static ItemStack fromString(String json) {
        JsonObject object = new JsonParser().parse(json).getAsJsonObject();

        String item64 = object.get("item").getAsString();

        String yaml = new String(Base64.getDecoder().decode(item64));
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(yaml);

        return config.getItemStack("item");
    }

    @Override
    public String toString() {
        return toString(this.itemStack);
    }

    public static String toString(ItemStack stack) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", stack);
        String yaml = config.saveToString();

        Map<String, Object> inventoryItem = new HashMap<>();
        inventoryItem.put("item", Base64.getEncoder().encodeToString(yaml.getBytes()));

        return gson.toJson(inventoryItem);
    }

    public ItemStack getItemFor(Player player, Plugin plugin) {
        ItemStack prepared = this.itemStack.clone();
        ItemMeta meta = prepared.getItemMeta();

        meta.setDisplayName(Text.format(meta.getDisplayName(), plugin).setReplacements(buildPlaceholders).getWithPlaceholders(player));

        if (meta.getLore() != null) {
            List<String> lore = new ArrayList<>();

            for (String line : meta.getLore()) {
                String formatted = Text.format(line, plugin).setReplacements(buildPlaceholders).getWithPlaceholders(player);
                if (formatted.isEmpty()) continue;
                lore.add(formatted);
            }
            meta.setLore(lore);
        }

        prepared.setItemMeta(meta);

        return prepared;

    }



}
