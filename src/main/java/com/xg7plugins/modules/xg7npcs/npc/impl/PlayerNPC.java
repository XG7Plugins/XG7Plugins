package com.xg7plugins.modules.xg7npcs.npc.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.modules.xg7npcs.event.NPCClickEvent;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import com.xg7plugins.modules.xg7npcs.living.impl.LivingPlayerNPC;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.skin.Skin;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Consumer;

public class PlayerNPC extends NPC {

    public static boolean USE_MANNEQUIN = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21_9);

    @Getter
    private final Skin skin;
    @Getter
    private final boolean usePlayerSkin;

    private final Consumer<NPCClickEvent> npcClickEventConsumer;

    public PlayerNPC(Plugin plugin, String id, Skin skin, Hologram hologram, Location spawnLocation, Map<EquipmentSlot, Item> equipments, boolean lookAtPlayer, boolean glow, Consumer<NPCClickEvent> npcClickEvent) {
        super(plugin, id, hologram, !USE_MANNEQUIN ? EntityTypes.PLAYER : EntityTypes.MANNEQUIN, spawnLocation, equipments, lookAtPlayer, glow);
        this.npcClickEventConsumer = npcClickEvent;
        this.skin = skin == null ? new Skin("ewogICJ0aW1lc3RhbXAiIDogMTczMDEzMjM2NjY3OCwKICAicHJvZmlsZUlkIiA6ICI3MGQzMzg2YzU5NzA0NmU1YWM4OTNhYmZlYTQ5N2IxMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJST1lMRUU1NDYwIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MyNjZlZTZiY2MyNjY1ZTBhODQ3N2Q0OTUzN2RkZjZiMjk4YjVjNGE1NDU2MWIyZjNjNDQ4MmI3N2IzNzA2MSIKICAgIH0KICB9Cn0=", "wOX7HhZ9VNtfNJi3GJFT3LnbXUCaFtpcyQDldoWvmrbA5RjrQ8H2jVcBXpMxnlk9U43KvNgFNxy/d3KklSNg9EfOBmo5H2GYICIx9iJOTCnOZC8GLhZWuia8jC7lqB6CfT7TdZWZAT2CXM2b8pteGWjoPg+OWUuXyg6Jg0k7uUrqzjMYjfh6y7hJXZIl38hMgISymrdQPGQVGTdBKeDmrQDveYn49ZYKdAbeb3pEHM5/QZIlvZVdvEHoLS4U5QRiw5V3/ERvd36RlKaydZVveqSMAoWvak/etVTiT3gLA5VbJN/qWYjz3rkmNboouYDC6eWy75b8TZSkPtk02JZ/ILDgpvYPyrAXwpZQNtWLXF99zun+aSZFPaSgW6/28yItmeJ0i+HpYbtOEGF6lJnEtI/jWNc0qb8/daE+HiahcKndpwi2zlErjlFfry08P3u5R7iX/KbGsgn96pVt+G9SXBRLX84ymWaqsg70xA+wgSov0xTc6AMHG15aHSrryw+RAikDbMU4ooNazDmeMWsitQNa8c120TPUQM/h+/ysNdksjnxDkyjOekzpyJmalGorfBe/KbRVqd2fK5VwIh4wJqWvPP2Gofh0C1sawQf2fu0KHHHg8XQhT+MivvrYzs0rccHnRiYcbDX3IPUGqoedaD3Q+Gkqo33XRqq+IJKlAFM=") : skin;
        this.usePlayerSkin = this.skin.getValue() == null && this.skin.getSignature() == null;
    }

    @Override
    public LivingNPC spawn(Player player) {



        LivingPlayerNPC playerNPC = new LivingPlayerNPC(player, this);
        playerNPC.spawn();
        XG7Plugins.getAPI().npcs().registerLivingNPC(playerNPC);
        return playerNPC;
    }

    @Override
    public void onClick(NPCClickEvent event) {
        if (npcClickEventConsumer != null) {
            npcClickEventConsumer.accept(event);
        }
    }
}
