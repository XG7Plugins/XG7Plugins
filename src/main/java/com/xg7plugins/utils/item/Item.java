package com.xg7plugins.utils.item;

import com.cryptomorin.xseries.XMaterial;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.parser.ItemParser;
import com.xg7plugins.utils.item.parser.impl.*;
import com.xg7plugins.utils.text.Text;
import dev.lone.itemsadder.api.CustomStack;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class Item implements Cloneable {

    private static final List<ItemParser<?>> parsers = new ArrayList<>();

    public static void registerParser(ItemParser<?> parser) {
        parsers.add(parser);
    }

    static {
        registerParser(new PotionParser());
        registerParser(new SkullParser());
        registerParser(new EnchantedBookParser());
        registerParser(new LeatherArmorParser());
        registerParser(new SimpleFireworkParser());
        registerParser(new SpawnerParser());
    }

    public static ItemParser<?> getParser(XMaterial material) {
        return parsers.stream().filter(p -> p.getMaterials().contains(material)).findFirst().orElse(null);
    }

    protected ItemStack itemStack;

    protected List<Pair<String,String>> buildPlaceholders = new ArrayList<>();


    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static Item from(ItemStack itemStack) {
        return new Item(itemStack);
    }
    public static Item from(XMaterial material) {
        return new Item(material.parseItem());
    }
    public static <T extends Item> T from(String material) {

        if (material == null) return (T) Item.from(Material.STONE);

        if (material.toUpperCase().startsWith("ITEMSADDER:")) {
            String id = material.substring(11);
            return (T) Item.from(CustomStack.getInstance(id).getItemStack());
        }

        if (material.split(", ").length == 2) {
            String[] args = material.split(", ");
            return (T) Item.from(new MaterialData(Material.getMaterial(args[0]), Byte.parseByte(args[1])));
        }

        String[] splitMaterial = material.split(":");

        String realMaterial = splitMaterial[0];


        XMaterial xMaterial = XMaterial.matchXMaterial(realMaterial).orElse(XMaterial.STONE);

        ItemParser<?> parser = getParser(xMaterial);

        if (parser != null) return (T) parser.parse(xMaterial, Arrays.copyOfRange(splitMaterial, 1, splitMaterial.length));

        return (T) Item.from(xMaterial);
    }
    public static Item from(CustomStack stack) {
        return Item.from(stack.getItemStack());
    }
    public static Item from(Material material) {
        return new Item(new ItemStack(material));
    }
    public static Item from(Material material, int amount) {
        return new Item(new ItemStack(material, amount));
    }
    public static Item from(XMaterial material, int amount) {
        ItemStack stack = material.parseItem();
        stack.setAmount(amount);
        return new Item(stack);
    }
    public static Item from(MaterialData data) {
        return new Item(data.toItemStack());
    }
    public static Item from(MaterialData data, int amount) {
        return new Item(data.toItemStack(amount));
    }
    public static Item air() {
        return new Item(new ItemStack(Material.AIR));
    }

    public static Item commandIcon(XMaterial material, CommandNode command) {
        Item item = new Item(material.parseItem());

        CommandSetup commandConfig = command.getCommand().getCommandSetup();
        CommandConfig methodConfig = command.getCommandMethod() != null ? command.getCommandMethod().getAnnotation(CommandConfig.class) : null;

        item.name("&b/&f" + commandConfig.name());
        item.lore(
                "lang:[commands-display.command-item.usage]",
                "lang:[commands-display.command-item.desc]",
                "lang:[commands-display.command-item.perm]",
                "lang:[commands-display.command-item.player-only]",
                command.getChildren().isEmpty() ? "" : "lang:[commands-display.if-subcommand]"
        );
        item.setBuildPlaceholders(
                Pair.of("syntax", methodConfig != null ? methodConfig.syntax() : commandConfig.syntax()),
                Pair.of("description", methodConfig != null ? methodConfig.description() : commandConfig.description()),
                Pair.of("permission", methodConfig != null ? methodConfig.syntax() :commandConfig.permission()),
                Pair.of("player_only", String.valueOf(methodConfig != null && methodConfig.isPlayerOnly()))
        );
        return item;
    }

    public InventoryItem toInventoryItem(Slot slot) {
        return (InventoryItem) new InventoryItem(itemStack, slot).setBuildPlaceholders(buildPlaceholders);
    }
    public InventoryItem toInventoryItem(int slot) {
        return (InventoryItem) new InventoryItem(itemStack, Slot.fromSlot(slot)).setBuildPlaceholders(buildPlaceholders);
    }
    public InventoryItem toInventoryItem(int slot, boolean ignoreBounds) {
        return (InventoryItem) new InventoryItem(itemStack, Slot.fromSlot(slot, ignoreBounds)).setBuildPlaceholders(buildPlaceholders);
    }

    public ClickableItem toClickableInventoryItem(Slot slot, Consumer<ActionEvent> clickEvent) {
        return toInventoryItem(slot).clickable(clickEvent);
    }
    public ClickableItem toClickableInventoryItem(int slot, Consumer<ActionEvent> clickEvent) {
        return toInventoryItem(slot).clickable(clickEvent);
    }
    public ClickableItem toClickableInventoryItem(int slot, boolean ignoreBounds, Consumer<ActionEvent> clickEvent) {
        return toInventoryItem(slot, true).clickable(clickEvent);
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I setBuildPlaceholders(Pair<String,String>... placeholders) {
        this.buildPlaceholders = Arrays.asList(placeholders);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I setBuildPlaceholders(List<Pair<String,String>> placeholders) {
        this.buildPlaceholders = placeholders;
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I amount(int amount) {
        this.itemStack.setAmount(amount);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I data(MaterialData data) {
        this.itemStack.setData(data);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I meta(ItemMeta meta) {
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I name(String name) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName("&r" + name);
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I lore(String... lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(Arrays.stream(lore).map(l -> "&r" + l).collect(Collectors.toList()));
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I lore(String lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I lore(List<String> lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(lore);
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I enchants(Map<Enchantment, Integer> enchants) {
        this.itemStack.addUnsafeEnchantments(enchants);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I enchant(Enchantment enchant, int level) {
        this.itemStack.addUnsafeEnchantment(enchant, level);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I flags(ItemFlag... flags) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addItemFlags(flags);
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I customModelData(int data) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setCustomModelData(data);
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I unbreakable(boolean unbreakable) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I setNBTTag(String key, Object value) {
        Gson gson = new Gson();


        if (this.itemStack.getType().equals(Material.AIR)) return (I) this;
        if (MinecraftVersion.isNewerThan(13)) {

            NamespacedKey namespacedKey = new NamespacedKey(XG7Plugins.getInstance().getJavaPlugin(), key);

            ItemMeta meta = this.itemStack.getItemMeta();

            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, gson.toJson(value));

            this.itemStack.setItemMeta(meta);
            return (I) this;
        }

        com.github.retrooper.packetevents.protocol.item.ItemStack nmsItem = SpigotReflectionUtil.decodeBukkitItemStack(this.itemStack);

        NBTCompound tag = nmsItem.getNBT();

        if (tag == null) tag = new NBTCompound();

        String jsonValue = gson.toJson(value);

        tag.setTag(key, new NBTString(jsonValue));

        nmsItem.setNBT(tag);
        this.itemStack = SpigotReflectionUtil.encodeBukkitItemStack(nmsItem);
        return (I) this;
    }

    public static <T> Optional<T> getTag(String key, ItemStack item, Class<T> clazz) {
        Gson gson = new Gson();

        if (item.getType().equals(Material.AIR)) return Optional.empty();

        if (MinecraftVersion.isNewerThan(13)) {

            NamespacedKey namespacedKey = new NamespacedKey(XG7Plugins.getInstance().getJavaPlugin(), key);

            ItemMeta meta = item.getItemMeta();

            if (!meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)) return Optional.empty();

            return Optional.ofNullable(gson.fromJson(meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING), clazz));
        }

        com.github.retrooper.packetevents.protocol.item.ItemStack nmsItem = SpigotReflectionUtil.decodeBukkitItemStack(item);

        NBTCompound tag = nmsItem.getNBT();

        if (tag == null) return Optional.empty();

        String jsonValue = tag.getStringTagValueOrNull(key);
        return Optional.ofNullable(gson.fromJson(jsonValue, clazz));

    }

    public <T> Optional<T> getTag(String key, Class<T> clazz) {
        return getTag(key, this.itemStack, clazz);
    }

    @SneakyThrows
    public static ItemStack fromJson(String json) {
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

    public ItemStack getItemFor(CommandSender player, Plugin plugin) {
        if (this.itemStack.getType().equals(Material.AIR)) return this.itemStack;

        ItemStack prepared = this.itemStack.clone();
        ItemMeta meta = prepared.getItemMeta();
        if (meta.getDisplayName() != null) {
            String newName = Text.detectLangs(player, plugin,meta.getDisplayName(), false).join().replaceAll(buildPlaceholders).textFor((Player) player).getText();
            meta.setDisplayName(newName.isEmpty() ? " " : newName);
        }

        if (meta.getLore() != null) {
            List<String> lore = new ArrayList<>();

            for (String line : meta.getLore()) {
                String formatted = Text.detectLangs(player, plugin,line, false).join().replaceAll(buildPlaceholders).textFor((Player) player).getText();
                if (ChatColor.stripColor(formatted).isEmpty()) continue;
                lore.add(formatted);
            }
            meta.setLore(lore);
        }

        prepared.setItemMeta(meta);


        return prepared;

    }

    public com.github.retrooper.packetevents.protocol.item.ItemStack toProtocolItemStack() {
        return SpigotReflectionUtil.decodeBukkitItemStack(this.itemStack);
    }

    public com.github.retrooper.packetevents.protocol.item.ItemStack toProtocolItemStack(CommandSender player, Plugin plugin) {
        return SpigotReflectionUtil.decodeBukkitItemStack(getItemFor(player, plugin));
    }

    public boolean isAir() {
        return this.itemStack == null || this.itemStack.getType().equals(Material.AIR);
    }


    @Override
    public Item clone() throws CloneNotSupportedException {
        return (Item) super.clone();
    }
}