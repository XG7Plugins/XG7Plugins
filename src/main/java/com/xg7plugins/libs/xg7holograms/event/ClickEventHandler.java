package com.xg7plugins.libs.xg7holograms.event;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.libs.xg7holograms.holograms.HologramState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClickEventHandler implements PacketListener {

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void onClick(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY)
            return;
        Player player = event.getPlayer();

        WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

        HologramState hologram = XG7Plugins.getInstance().getHologramsManager().getHologramState(player);

        if (hologram == null) return;

        ClickType type;

        switch (packet.getAction()) {
            case INTERACT:
                type = player.isSneaking() ? ClickType.SHIFT_RIGHT_CLICK : ClickType.RIGHT_CLICK;
                break;
            case ATTACK:
                type = player.isSneaking() ? ClickType.SHIFT_LEFT_CLICK : ClickType.LEFT_CLICK;
                break;
            default:
                type = ClickType.RIGHT_CLICK;
                break;
        }


        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(new HologramClickEvent(player, type, hologram)));

    }
}
