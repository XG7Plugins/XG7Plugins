package com.xg7plugins.modules.xg7dialogs.button.action.actions;

import com.github.retrooper.packetevents.protocol.chat.clickevent.CustomClickEvent;
import com.github.retrooper.packetevents.protocol.dialog.action.Action;
import com.github.retrooper.packetevents.protocol.dialog.action.StaticAction;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.xg7plugins.modules.xg7dialogs.button.action.DialogButtonAction;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class CustomAction implements DialogButtonAction {

    private final ResourceLocation resourceLocation;
    private final NBT payload;

    @Override
    public Action build(Dialog dialog, Player player) {
        return new StaticAction(new CustomClickEvent(
                resourceLocation,
                payload
        ));
    }
}
