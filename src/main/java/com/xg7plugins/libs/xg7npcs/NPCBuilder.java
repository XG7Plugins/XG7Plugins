package com.xg7plugins.libs.xg7npcs;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7npcs.npcs.NPC;
import com.xg7plugins.libs.xg7npcs.npcs.NPC1_17_1_XX;
import com.xg7plugins.libs.xg7npcs.npcs.NPC1_7;
import com.xg7plugins.libs.xg7npcs.npcs.NPC1_8_1_16;
import com.xg7plugins.utils.Builder;
import com.xg7plugins.utils.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class NPCBuilder extends Builder<NPC> {

    private Object name;
    private Object skin;
    private String skinComplement;
    private Location location;
    private String id;
    private HashMap<Integer, ItemStack> equipments;
    private Plugin plugin;
    private boolean lookAtPlayer = false;
    private boolean playerSkin = false;

    public NPCBuilder(Plugin plugin, String id) {
        this.plugin = plugin;
        this.id = id;
        this.equipments = new HashMap<>();
    }

    public NPCBuilder setName(String... strings) {
        if (XG7Plugins.getMinecraftVersion() < 8) {
            this.name = strings[0];
        } else {
            this.name = Arrays.stream(strings).collect(Collectors.toList());
        }
        return this;
    }
    public NPCBuilder setLocation(Location location) {
        this.location = location;
        return this;
    }
    public NPCBuilder lookAtPlayer(boolean lookAtPlayer) {
        this.lookAtPlayer = lookAtPlayer;
        return this;
    }
    public NPCBuilder withPlayerSkin(boolean playerSkin) {
        this.playerSkin = playerSkin;
        return this;
    }
    public NPCBuilder setEquipments(ItemStack mainHand, ItemStack offHand, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.equipments = new HashMap<>();

        equipments.put(0, mainHand);
        equipments.put(1, offHand);
        equipments.put(2, helmet);
        equipments.put(3, chestplate);
        equipments.put(4, leggings);
        equipments.put(5, boots);

        return this;
    }
    public NPCBuilder setMainHand(ItemStack mainHand) {
        this.equipments.put(0, mainHand);
        return this;
    }
    public NPCBuilder setOffHand(ItemStack offHand) {
        this.equipments.put(1, offHand);
        return this;
    }
    public NPCBuilder setHelmet(ItemStack helmet) {
        this.equipments.put(2, helmet);
        return this;
    }
    public NPCBuilder setChestplate(ItemStack chestplate) {
        this.equipments.put(3, chestplate);
        return this;
    }
    public NPCBuilder setLeggings(ItemStack leggings) {
        this.equipments.put(4, leggings);
        return this;
    }
    public NPCBuilder setBoots(ItemStack boots) {
        this.equipments.put(5, boots);
        return this;
    }
    public NPCBuilder setSkin(Object value) {
        this.skin = value;
        return this;
    }
    public NPCBuilder setSkin(String value, String complement) {
        this.skin = value;
        this.skinComplement = complement;
        return this;
    }


    public NPC build(Object... args) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }

        NPC npc =
                XG7Plugins.getMinecraftVersion() < 8 ? new NPC1_7(plugin,id, (String) name,location) :
                XG7Plugins.getMinecraftVersion() < 17 ? new NPC1_8_1_16(plugin,id, (List<String>) name,location) :
                new NPC1_17_1_XX(plugin,id, (List<String>) name,location);

        npc.setLookAtPlayer(lookAtPlayer);
        npc.setPlayerSkin(playerSkin);
        if (!equipments.isEmpty()) {
            npc.setEquipment(equipments.get(0),equipments.get(1),equipments.get(2),equipments.get(3),equipments.get(4),equipments.get(5));
        }

        if (skin != null) {
            if (skin instanceof Player) {
                npc.setSkin((Player) skin);
            } else if (skin instanceof String) {
                try {
                    npc.setSkin((String) skin);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (skinComplement != null) {
                npc.setSkin((String) skin, skinComplement);
            }
        }

        return npc;
    }

    public static NPCBuilder creator(Plugin plugin, String id) {
        return new NPCBuilder(plugin,id);
    }


}
