package com.xg7plugins.utils.item;

import com.cryptomorin.xseries.XMaterial;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.google.gson.*;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.http.HTTP;
import com.xg7plugins.utils.http.HTTPResponse;
import com.xg7plugins.utils.item.parser.ItemParser;
import com.xg7plugins.utils.item.parser.impl.*;
import com.xg7plugins.utils.text.Text;
import dev.lone.itemsadder.api.CustomStack;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
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

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A utility class for creating and manipulating ItemStack objects with various properties and metadata.
 */
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

    private static final ObjectCache<Material, String> imageCache = new ObjectCache<>(
            XG7Plugins.getInstance(),
            15 * 60 * 1000L,
            true,
            "item-image-cache",
            false,
            Material.class,
            String.class
    );


    /**
     * Get the parser for the given material
     * @param material The material to get the parser for
     * @return The parser for the given material, or null if none found
     */
    public static ItemParser<?> getParser(XMaterial material) {
        return parsers.stream().filter(p -> p.getMaterials().contains(material)).findFirst().orElse(null);
    }

    protected ItemStack itemStack;

    protected List<Pair<String,String>> buildPlaceholders = new ArrayList<>();

    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * Create an Item from an ItemStack
     * @param itemStack The ItemStack to create the Item from
     * @return The created Item
     */
    public static Item from(ItemStack itemStack) {
        return new Item(itemStack);
    }

    /**
     * Create an Item from an XMaterial
     * @param material The XMaterial to create the Item from
     * @return The created Item
     */
    public static Item from(XMaterial material) {
        return new Item(material.parseItem());
    }

    /**
     * Create an Item from a string representation
     * @param material The string representation of the Item
     * @return The created Item
     * @param <T> The type of the Item
     */
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

        XMaterial xMaterial = XMaterial.matchXMaterial(realMaterial.toUpperCase()).orElse(XMaterial.STONE);

        ItemParser<?> parser = getParser(xMaterial);

        if (parser != null) return (T) parser.parse(xMaterial, Arrays.copyOfRange(splitMaterial, 1, splitMaterial.length));

        return (T) Item.from(xMaterial);
    }

    /**
     *  Create an Item from an ItemsAdder's CustomStack
     * @param stack The CustomStack to create the Item from
     * @return The created Item
     */
    public static Item from(CustomStack stack) {
        return Item.from(stack.getItemStack());
    }

    /**
     * Create an Item from a Material
     * @param material The Material to create the Item from
     * @return The created Item
     */
    public static Item from(Material material) {
        return new Item(new ItemStack(material));
    }

    /**
     * Create an Item from a Material and amount
     * @param material The Material to create the Item from
     * @param amount The amount of the Item
     * @return The created Item
     */
    public static Item from(Material material, int amount) {
        return new Item(new ItemStack(material, amount));
    }

    /**
     * Create an Item from an XMaterial and amount
     * @param material The XMaterial to create the Item from
     * @param amount The amount of the Item
     * @return The created Item
     */
    public static Item from(XMaterial material, int amount) {
        ItemStack stack = material.parseItem();
        stack.setAmount(amount);
        return new Item(stack);
    }

    /**
     * Create an Item from MaterialData
     * @param data The MaterialData to create the Item from
     * @return The created Item
     */
    public static Item from(MaterialData data) {
        return new Item(data.toItemStack());
    }

    /**
     * Create an Item from MaterialData and amount
     * @param data The MaterialData to create the Item from
     * @param amount The amount of the Item
     * @return The created Item
     */
    public static Item from(MaterialData data, int amount) {
        return new Item(data.toItemStack(amount));
    }

    /**
     * Create a null Item (AIR)
     * @return The created Item
     */
    public static Item air() {
        return new Item(new ItemStack(Material.AIR));
    }

    /**
     * Create a command icon Item for a given command
     * for command menus.
     *
     * @param material Material of the command icon
     * @param command The command node
     * @return The created command icon Item
     */
    public static Item commandIcon(XMaterial material, CommandNode command) {

        Item item = new Item(material.parseItem());

        CommandSetup setup = command.getCommand().getCommandSetup();
        CommandConfig methodConfig = command.getCommandMethod().getAnnotation(CommandConfig.class);

        boolean isRoot = command.getParent() == null;

        item.name("&f" + (isRoot ? setup.name() : methodConfig.name()));
        item.lore(
                "lang:[commands-display.command-item.usage]",
                "lang:[commands-display.command-item.desc]",
                "lang:[commands-display.command-item.perm]",
                "lang:[commands-display.command-item.player-only]",
                command.getChildren().isEmpty() ? "" : "lang:[commands-display.if-subcommand]"
        );
        item.setBuildPlaceholders(
                Pair.of("syntax", isRoot ? setup.syntax() : methodConfig.syntax()),
                Pair.of("description", isRoot ? setup.description() : methodConfig.description()),
                Pair.of("permission", isRoot ? setup.permission() : methodConfig.permission()),
                Pair.of("player_only", String.valueOf(methodConfig.isPlayerOnly()))
        );
        return item;
    }

    public Item type(XMaterial material) {

        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_13)) this.itemStack.setData(new MaterialData(material.get(), material.getData()));
        else this.itemStack.setType(material.get());

        return this;
    }

    public Item type(Material material) {
        this.itemStack.setType(material);
        return this;
    }


    /**
     * Convert this Item to an InventoryItem for a given slot
     * @param slot The slot to create the InventoryItem for
     * @return The created InventoryItem
     */
    public InventoryItem toInventoryItem(Slot slot) {
        return (InventoryItem) new InventoryItem(itemStack, slot).setBuildPlaceholders(buildPlaceholders);
    }

    /**
     * Convert this Item to an InventoryItem for a given slot index
     * @param slot The slot index to create the InventoryItem for
     * @return The created InventoryItem
     */
    public InventoryItem toInventoryItem(int slot) {
        return new InventoryItem(itemStack, Slot.fromSlot(slot)).setBuildPlaceholders(buildPlaceholders);
    }

    /**
     * Convert this Item to an InventoryItem for a given slot index
     * @param slot The slot index to create the InventoryItem for
     * @param ignoreBounds Whether to ignore bounds checking for the slot
     * @return The created InventoryItem
     */
    public InventoryItem toInventoryItem(int slot, boolean ignoreBounds) {
        return new InventoryItem(itemStack, Slot.fromSlot(slot, ignoreBounds)).setBuildPlaceholders(buildPlaceholders);
    }

    /**
     * Convert this Item to a ClickableItem for a given slot and click event
     * @param slot The slot to create the ClickableItem for
     * @param clickEvent The click event to assign to the ClickableItem
     * @return The created ClickableItem
     */
    public ClickableItem toClickableInventoryItem(Slot slot, Consumer<ActionEvent> clickEvent) {
        return toInventoryItem(slot).clickable(clickEvent);
    }

    /**
     * Convert this Item to a ClickableItem for a given slot index and click event
     * @param slot The slot index to create the ClickableItem for
     * @param clickEvent The click event to assign to the ClickableItem
     * @return The created ClickableItem
     */
    public ClickableItem toClickableInventoryItem(int slot, Consumer<ActionEvent> clickEvent) {
        return toInventoryItem(slot).clickable(clickEvent);
    }

    /**
     * Convert this Item to a ClickableItem for a given slot index, ignoring bounds, and click event
     * @param slot The slot index to create the ClickableItem for
     * @param ignoreBounds Whether to ignore bounds checking for the slot
     * @param clickEvent The click event to assign to the ClickableItem
     * @return The created ClickableItem
     */
    public ClickableItem toClickableInventoryItem(int slot, boolean ignoreBounds, Consumer<ActionEvent> clickEvent) {
        return toInventoryItem(slot, true).clickable(clickEvent);
    }

    /**
     * Set build placeholders for this Item
     * @param placeholders The placeholders to set
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I setBuildPlaceholders(Pair<String,String>... placeholders) {
        this.buildPlaceholders = Arrays.asList(placeholders);
        return (I) this;
    }

    /**
     * Set build placeholders for this Item
     * @param placeholders The placeholders to set
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I setBuildPlaceholders(List<Pair<String,String>> placeholders) {
        this.buildPlaceholders = placeholders;
        return (I) this;
    }

    /**
     * Set the amount of this Item
     * @param amount The amount to set
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I amount(int amount) {
        this.itemStack.setAmount(amount);
        return (I) this;
    }

    /**
     * Set the MaterialData of this Item
     * @param data The MaterialData to set
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I data(MaterialData data) {
        this.itemStack.setData(data);
        return (I) this;
    }

    /**
     * Set the ItemMeta of this Item
     * @param meta The ItemMeta to set
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I meta(ItemMeta meta) {
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    /**
     * Set the name of this Item
     * @param name The name to set
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I name(String name) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName("&r" + name);
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    /**
     * Set the lore of this Item
     * @param lore The lore to set
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I lore(String... lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(Arrays.stream(lore).map(l -> "&r" + l).collect(Collectors.toList()));
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    /**
     * Set the lore of this Item
     * @param lore The lore to set
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I lore(String lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    /**
     * Set the lore of this Item
     * @param lore The lore to set
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I lore(List<String> lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(lore);
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    /**
     * Add multiple enchantments to this Item
     * @param enchants The enchantments to add
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I enchants(Map<Enchantment, Integer> enchants) {
        this.itemStack.addUnsafeEnchantments(enchants);
        return (I) this;
    }

    /**
     * Add a single enchantment to this Item
     * @param enchant The enchantment to add
     * @param level The level of the enchantment
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I enchant(Enchantment enchant, int level) {
        this.itemStack.addUnsafeEnchantment(enchant, level);
        return (I) this;
    }

    /**
     * Add item flags to this Item
     * @param flags The item flags to add
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I flags(ItemFlag... flags) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addItemFlags(flags);
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    /**
     * Set the custom model data of this Item
     * @param data The custom model data to set
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I customModelData(int data) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setCustomModelData(data);
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    /**
     * Set whether this Item is unbreakable
     * @param unbreakable Whether the Item is unbreakable
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I unbreakable(boolean unbreakable) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(meta);
        return (I) this;
    }

    /**
     * Set a custom NBT tag on this Item
     * @param key The key of the NBT tag
     * @param value The value of the NBT tag
     * @return The Item instance
     * @param <I> The type of the Item
     */
    @SuppressWarnings("unchecked")
    public <I extends Item> I setNBTTag(String key, Object value) {
        Gson gson = new Gson();


        if (this.itemStack.getType().equals(Material.AIR)) return (I) this;
        if (MinecraftServerVersion.isNewerThan(ServerVersion.V_1_13)) {

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

    /**
     * Get a custom NBT tag from an ItemStack
     * @param key The key of the NBT tag
     * @param item The ItemStack to get the NBT tag from
     * @param clazz The class of the NBT tag value
     * @return An Optional containing the NBT tag value, or empty if not found
     * @param <T> The type of the NBT tag value
     */
    public static <T> Optional<T> getTag(String key, ItemStack item, Class<T> clazz) {
        Gson gson = new Gson();

        if (item.getType().equals(Material.AIR)) return Optional.empty();

        if (MinecraftServerVersion.isNewerThan(ServerVersion.V_1_13)) {

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

    /**
     * Convert an ItemStack to a string representation
     * @param stack The ItemStack to convert
     * @return The string representation of the ItemStack
     */
    public static String toString(ItemStack stack) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", stack);
        String yaml = config.saveToString();

        Map<String, Object> inventoryItem = new HashMap<>();
        inventoryItem.put("item", Base64.getEncoder().encodeToString(yaml.getBytes()));

        return gson.toJson(inventoryItem);
    }

    /**
     * Get the ItemStack for a given player, applying placeholders
     * @param player The player to get the ItemStack for
     * @param plugin The plugin instance
     * @return The ItemStack for the player
     */
    public ItemStack getItemFor(CommandSender player, Plugin plugin) {
        if (this.itemStack.getType().equals(Material.AIR)) return this.itemStack;

        ItemStack prepared = this.itemStack.clone();
        ItemMeta meta = prepared.getItemMeta();
        if (meta.getDisplayName() != null) {
            String newName = Text.detectLangs(player, plugin,meta.getDisplayName(), false).replaceAll(buildPlaceholders).textFor((Player) player).getText();
            meta.setDisplayName(newName.isEmpty() ? " " : newName);
        }

        if (meta.getLore() != null) {
            List<String> lore = new ArrayList<>();

            for (String line : meta.getLore()) {
                String formatted = Text.detectLangs(player, plugin,line, false).replaceAll(buildPlaceholders).textFor((Player) player).getText();
                if (ChatColor.stripColor(formatted).isEmpty()) continue;
                lore.add(formatted);
            }
            meta.setLore(lore);
        }

        prepared.setItemMeta(meta);


        return prepared;

    }

    /**
     * Convert this Item to a PacketEvents protocol ItemStack
     * @return The protocol ItemStack
     */
    public com.github.retrooper.packetevents.protocol.item.ItemStack toProtocolItemStack() {
        return SpigotConversionUtil.fromBukkitItemStack(this.itemStack);
    }

    /**
     * Convert this Item to a PacketEvents protocol ItemStack for a given player
     * @param player The player to get the ItemStack for
     * @param plugin The plugin instance
     * @return The protocol ItemStack for the player
     */
    public com.github.retrooper.packetevents.protocol.item.ItemStack toProtocolItemStack(CommandSender player, Plugin plugin) {
        return SpigotConversionUtil.fromBukkitItemStack(getItemFor(player, plugin));
    }

    /**
     * Check if this Item is AIR
     * @return True if the Item is AIR, false otherwise
     */
    public boolean isAir() {
        return this.itemStack == null || this.itemStack.getType().equals(Material.AIR);
    }

    private static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public static CompletableFuture<String> requestItemIcon(Material material) {
        if (material != null && imageCache.containsKey(material).join()) {
            return imageCache.get(material);
        }

        return CompletableFuture.supplyAsync(() -> {

            String nameBukkit = material == null ? "air" : material.name().toLowerCase().replace("_", " ");
            String name = Character.toUpperCase(nameBukkit.charAt(0)) + nameBukkit.substring(1);

            List<String> variants = Arrays.asList(
                    name,
                    toTitleCase(name),
                    name.toLowerCase(),
                    name.replace(" ", "_"),
                    name.toLowerCase().replace(" ", "_")
            );



            JsonObject root = null;

            for (String variant : variants) {
                try {

                    String encodedTitle = URLEncoder.encode("File:" + variant + ".png", "UTF-8");

                    root = HTTP.get(
                            "https://minecraft.wiki/api.php?" +
                                    "action=query&" +
                                    "prop=imageinfo&" +
                                    "iiprop=url&" +
                                    "format=json&" +
                                    "redirects=1&" +
                                    "titles=" + encodedTitle,
                            Collections.singletonList(Pair.of("Accept", "application/json"))
                    ).getJson();

                    JsonObject query = root.getAsJsonObject("query");
                    JsonObject pages = query.getAsJsonObject("pages");
                    Map.Entry<String, JsonElement> pageEntry = pages.entrySet().iterator().next();
                    JsonObject page = pageEntry.getValue().getAsJsonObject();
                    JsonArray imageInfo = page.getAsJsonArray("imageinfo");

                    if (imageInfo == null) continue;

                    String url = imageInfo
                            .get(0)
                            .getAsJsonObject()
                            .get("url")
                            .getAsString();

                    if (material != null) {
                        imageCache.put(material, url);
                    }

                    return url;
                } catch (Exception ignored) {
                }
            }


            if (root == null) {
                throw new RuntimeException("Failed to load image for " + name);
            }

            return "";
        });
    }

    public CompletableFuture<String> requestItemIcon() {
        return requestItemIcon(this.itemStack == null ? null : this.itemStack.getType());
    }

    @Override
    public Item clone() throws CloneNotSupportedException {
        Item item = (Item) super.clone();
        return new Item(this.itemStack.clone());
    }
}