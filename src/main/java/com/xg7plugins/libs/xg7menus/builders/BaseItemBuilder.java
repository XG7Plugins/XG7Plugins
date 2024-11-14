package com.xg7plugins.libs.xg7menus.builders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.item.SkullItemBuilder;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class BaseItemBuilder<B extends BaseItemBuilder<B>> {


    protected ItemStack itemStack;
    private final Plugin plugin;
    @Getter
    private Consumer<ClickEvent> event;
    @Getter
    private HashMap<String, String> buildReplacements = new HashMap<>();

    public BaseItemBuilder(ItemStack stack, Plugin plugin) {
        this.itemStack = stack;
        this.plugin = plugin;
    }
    public B setBuildReplacements(HashMap<String, String> replacements) {
        this.buildReplacements = replacements;
        return (B) this;
    }
    public B setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return (B) this;
    }
    public B setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return (B) this;
    }
    public B data(MaterialData data) {
        this.itemStack.setData(data);
        return (B) this;
    }
    public B meta(ItemMeta meta) {
        this.itemStack.setItemMeta(meta);
        return (B) this;
    }
    public B addEnchant(Enchantment enchant, int level) {
        this.itemStack.addUnsafeEnchantment(enchant, level);
        return (B) this;
    }
    public B addEnchants(Map<Enchantment, Integer> enchants) {
        this.itemStack.addUnsafeEnchantments(enchants);
        return (B) this;
    }
    public B lore(@NotNull List<String> lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(lore.stream().map(text -> Text.format(text,plugin).getText()).collect(Collectors.toList()));
        meta(meta);
        return (B) this;
    }
    public B lore(@NotNull List<String> lore, HashMap<String, String> replacements) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(lore.stream().map(text -> Text.format(text,plugin).setReplacements(replacements).getText()).collect(Collectors.toList()));
        meta(meta);
        return (B) this;
    }
    public B name(String name) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(Text.format(name,plugin).getText());
        meta(meta);
        return (B) this;
    }
    public B name(String name, HashMap<String, String> replacements) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(Text.format(name,plugin).setReplacements(replacements).getText());
        meta(meta);
        return (B) this;
    }
    public B addFlags(ItemFlag... flags) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addItemFlags(flags);
        meta(meta);
        return (B) this;
    }
    public B setCustomModelData(int data) {
        if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", "")) < 9) return (B) this;
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setCustomModelData(data);
        meta(meta);
        return (B) this;
    }
    @SneakyThrows
    public B unbreakable(boolean unbreakable) {
        ItemMeta meta = this.itemStack.getItemMeta();
        try {
            meta.setUnbreakable(unbreakable);
        } catch (Exception ignored) {
            Object spigot = meta.getClass().getMethod("spigot").invoke(meta);
            spigot.getClass().getMethod("setUnbreakable", Boolean.class).invoke(spigot, unbreakable);
        }
        meta(meta);
        return (B) this;
    }
    public B click(Consumer<ClickEvent> event) {
        this.event = event;
        return (B) this;
    }
    public B setPlaceHolders(Player player) {
        if (itemStack.getItemMeta() == null) return (B) this;
        if (itemStack.getItemMeta().getDisplayName() != null) name(Text.format(itemStack.getItemMeta().getDisplayName(),plugin).getWithPlaceholders(player));
        if (itemStack.getItemMeta().getLore() != null) lore(itemStack.getItemMeta().getLore().stream().map(l -> Text.format(l,plugin).getWithPlaceholders(player)).collect(Collectors.toList()));
        return (B) this;
    }
    public B setPlaceHolders(Player player, HashMap<String, String> replacements) {
        if (itemStack.getItemMeta() == null) return (B) this;
        if (itemStack.getItemMeta().getDisplayName() != null) name(Text.format(itemStack.getItemMeta().getDisplayName(),plugin).setReplacements(replacements).getWithPlaceholders(player));
        if (itemStack.getItemMeta().getLore() != null) lore(itemStack.getItemMeta().getLore().stream().map(l -> Text.format(l,plugin).setReplacements(replacements).getWithPlaceholders(player)).collect(Collectors.toList()));
        return (B) this;
    }
    @SneakyThrows
    public B addOrModifyNBTTag(String key, Object value) {

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
        return (B) this;
    }

    @SneakyThrows
    public static <T> T getNBTTagValue(ItemStack stack, String key, Class<T> clazz) {
        Gson gson = new Gson();

        ReflectionClass nmsItemStackClass = NMSUtil.getNMSClass("ItemStack");
        ReflectionClass craftItemStackClass = NMSUtil.getCraftBukkitClass("inventory.CraftItemStack");

        ReflectionObject nmsItem = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invokeToRObject(stack);
        ReflectionObject tag = nmsItemStackClass.getMethod("getTag").invokeToRObject(nmsItem);

        if (tag != null) {
            String jsonValue = tag.getMethod("getString", String.class).invoke(key);
            return gson.fromJson(jsonValue, clazz);
        }
        return null;
    }

    public ItemStack toItemStack() {
        return this.itemStack;
    }

    public static BaseItemBuilder chose(boolean chose, BaseItemBuilder item1, BaseItemBuilder item2) {
        return chose ? item1 : item2;
    }
    public static BaseItemBuilder choseByNewerVersion(int version, BaseItemBuilder item1, BaseItemBuilder item2) {
        return Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", "")) > version ? item1 : item2;
    }
    public static BaseItemBuilder choseByOlderVersion(int version, BaseItemBuilder item1, BaseItemBuilder item2) {
        return Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", "")) < version ? item1 : item2;
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

    public static <B extends BaseItemBuilder<B>> B from(String material, Plugin plugin) {

        if (material == null) return (B) ItemBuilder.from(Material.STONE,plugin);
        if (material.startsWith("eyJ0")) return (B) new SkullItemBuilder(plugin).setValue(material);
        if (material.equals("THIS_PLAYER")) return (B) new SkullItemBuilder(plugin).renderSkullPlayer();
        if (material.split(", ").length == 2) {
            String[] args = material.split(", ");
            return (B) ItemBuilder.from(new MaterialData(XMaterial.valueOf(args[0]).parseMaterial(), Byte.parseByte(args[0])),plugin);
        }

        return (B) ItemBuilder.from(XMaterial.valueOf(material).parseItem(),plugin);
    }
}
