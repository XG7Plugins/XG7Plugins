package com.xg7plugins.libs.xg7npcs.event;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.events.packetevents.PacketEventHandler;
import com.xg7plugins.libs.xg7npcs.npcs.NPC;
import com.xg7plugins.utils.reflection.ReflectionObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClickEventHandler implements PacketListener {

    @Override
    public boolean isEnabled() {
        return true;
    }


    @PacketEventHandler(packet = "PacketPlayInUseEntity")
    public Object onClick(Player player, ReflectionObject packet) {
        try {
            NPC hologram = XG7Plugins.getInstance().getNpcManager().getNPCByID(player, packet.getField(XG7Plugins.getMinecraftVersion() > 20 ? "b" : "a"));

            if (hologram == null) return packet.getObject();

            Enum<?> enumAction = XG7Plugins.getMinecraftVersion() <= 16 ? packet.getField("action") : ReflectionObject.of(packet.getField(XG7Plugins.getMinecraftVersion() > 20 ? "c" : "b")).getMethod("a").invoke() ;


            ClickType type;

            switch (enumAction.name()) {
                case "b":
                case "c":
                case "INTERACT_AT":
                case "INTERACT":
                    type = player.isSneaking() ? ClickType.SHIFT_RIGHT_CLICK : ClickType.RIGHT_CLICK;
                    break;
                case "a":
                case "ATTACK":
                    type = player.isSneaking() ? ClickType.SHIFT_LEFT_CLICK : ClickType.LEFT_CLICK;
                    break;
                default:
                    type = ClickType.RIGHT_CLICK;
                    break;
            }


            Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(new NPCClickEvent(player, type, hologram)));

            return packet.getObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet.getObject();
    }
}
