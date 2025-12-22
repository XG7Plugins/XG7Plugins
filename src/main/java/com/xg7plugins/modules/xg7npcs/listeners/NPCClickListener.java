package com.xg7plugins.modules.xg7npcs.listeners;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.events.packetevents.PacketListenerSetup;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.xg7plugins.modules.xg7npcs.event.ClickAction;
import com.xg7plugins.modules.xg7npcs.event.NPCClickEvent;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;

@PacketListenerSetup
public class NPCClickListener implements PacketListener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Set<PacketTypeCommon> getHandledEvents() {
        return Collections.singleton(PacketType.Play.Client.INTERACT_ENTITY);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {

        WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

        boolean isSneaking = ((Player) event.getPlayer()).isSneaking();

        int entityId = packet.getEntityId();

        WrapperPlayClientInteractEntity.InteractAction action = packet.getAction();

        ClickAction clickAction = isSneaking ? ClickAction.SHIFT_RIGHT : ClickAction.RIGHT_CLICK;

        if (action == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
            clickAction = isSneaking ? ClickAction.SHIFT_LEFT : ClickAction.LEFT_CLICK;
        }

        if (XG7Plugins.getAPI().cooldowns().containsPlayer("xg7npcs_click_cooldown", event.getPlayer())) return;

        for (LivingNPC livingNPC : XG7Plugins.getAPI().npcs().getAllLivingNPCs()) {

            if (livingNPC.getSpawnedEntityID() != entityId) continue;

            NPCClickEvent clickEvent = new NPCClickEvent(livingNPC.getPlayer(), livingNPC, clickAction);

            livingNPC.getNPC().onClick(clickEvent);

            XG7Plugins.getAPI().cooldowns().addCooldown(livingNPC.getPlayer(), "xg7npcs_click_cooldown", 100L);

            return;
        }
    }
}
