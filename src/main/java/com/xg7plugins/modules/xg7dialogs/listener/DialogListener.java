package com.xg7plugins.modules.xg7dialogs.listener;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.nbt.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCustomClickAction;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.events.packetevents.PacketListenerSetup;
import com.xg7plugins.modules.xg7dialogs.XG7Dialogs;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.modules.xg7dialogs.dialogs.DialogResponseHandler;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.tasks.tasks.BukkitTask;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@PacketListenerSetup
public class DialogListener implements PacketListener {
    @Override
    public boolean isEnabled() {
        return MinecraftServerVersion.isNewerOrEqual(ServerVersion.V_1_21_6);
    }

    @Override
    public Set<PacketTypeCommon> getHandledEvents() {
        return Collections.singleton(PacketType.Play.Client.CUSTOM_CLICK_ACTION);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {

        WrapperPlayClientCustomClickAction packet = new WrapperPlayClientCustomClickAction(event);

        Player player = event.getPlayer();
        Dialog dialog = XG7Dialogs.getWaitingDialog(player.getUniqueId());
        if  (dialog == null) return;

        ResourceLocation id = packet.getId();
        if (!id.getNamespace().equals(dialog.getNamespace())) return;

        XG7Dialogs.unregisterWaitingDialog(player.getUniqueId());

        if (dialog.getResponseHandler() == null) return;
        DialogResponseHandler responseHandler = dialog.getResponseHandler();

        String channel = id.getKey();
        Map<String, Object> payload = new HashMap<>();
        NBT data = packet.getPayload();

        NBTCompound compound = data instanceof NBTCompound ? (NBTCompound) data : new NBTCompound();
        for (Map.Entry<String, NBT> entry : compound.getTags().entrySet()) {
            String key = entry.getKey();
            Object value = convertNBT(entry.getValue());

            payload.put(key, value);
        }

        XG7Plugins.getAPI().taskManager().runSync(BukkitTask.of(() -> responseHandler.onResponse(channel, payload)));
    }

    private static Object convertNBT(NBT value) {
        if (value instanceof NBTInt) return ((NBTInt) value).getAsInt();
        else if (value instanceof NBTString) return ((NBTString) value).getValue();
        else if (value instanceof NBTLong)  return ((NBTLong) value).getAsLong();
        else if (value instanceof NBTDouble) return ((NBTDouble) value).getAsDouble();
        else if (value instanceof NBTFloat) return ((NBTFloat) value).getAsFloat();
        else if (value instanceof NBTByte) return ((NBTByte) value).getAsBool();
        else if (value instanceof NBTByteArray) return ((NBTByteArray) value).getValue();
        else return value;
    }
}
