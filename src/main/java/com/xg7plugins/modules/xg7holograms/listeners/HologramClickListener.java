package com.xg7plugins.modules.xg7holograms.listeners;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.PacketListener;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.xg7plugins.events.packetevents.PacketListenerSetup;
import com.xg7plugins.modules.xg7holograms.event.ClickAction;
import com.xg7plugins.modules.xg7holograms.event.HologramClickEvent;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;

@PacketListenerSetup
public class HologramClickListener implements PacketListener {
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

        if (XG7Plugins.getAPI().cooldowns().containsPlayer("xg7holograms_click_cooldown", event.getPlayer())) return;

        for (LivingHologram livingHologram : XG7Plugins.getAPI().holograms().getAllLivingHolograms()) {

            Hologram hologram = livingHologram.checkHologramByEntityID(entityId);

            if (hologram == null) continue;

            HologramClickEvent clickEvent = new HologramClickEvent(livingHologram.getPlayer(), livingHologram, clickAction);

            hologram.onClick(clickEvent);

            XG7Plugins.getAPI().cooldowns().addCooldown(livingHologram.getPlayer(), "xg7holograms_click_cooldown", 100L);

            return;
        }
    }
}
