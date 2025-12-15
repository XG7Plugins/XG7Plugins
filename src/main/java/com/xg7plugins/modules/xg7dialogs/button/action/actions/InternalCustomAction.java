package com.xg7plugins.modules.xg7dialogs.button.action.actions;

import com.github.retrooper.packetevents.protocol.dialog.action.Action;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.xg7plugins.modules.xg7dialogs.button.action.DialogButtonAction;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class InternalCustomAction implements DialogButtonAction {

    private final String channel;
    private final NBTCompound additions;

    @Override
    public Action build(Dialog dialog, Player player) {
        return new com.github.retrooper.packetevents.protocol.dialog.action.DynamicCustomAction(
                new ResourceLocation(dialog.getNamespace() + ":" + channel),
                additions
        );
    }
}
