package com.xg7plugins.modules.xg7dialogs.button.action;

import com.github.retrooper.packetevents.protocol.dialog.action.Action;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.xg7plugins.modules.xg7dialogs.button.action.actions.*;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import org.bukkit.entity.Player;

public interface DialogAction {

    Action build(Dialog dialog, Player player);

    static ChangePageAction changePage(int number) {
        return new ChangePageAction(number);
    }

    static SuggestCommandAction suggestCommand(String command) {
        return new SuggestCommandAction(command);
    }

    static ShowDialogAction showDialog(Dialog dialog) {
        return new ShowDialogAction(dialog);
    }

    static RunCommandAction runCommand(String command) {
        return new RunCommandAction(command);
    }

    static OpenURLAction openURL(String url) {
        return new OpenURLAction(url);
    }

    static CopyToClipboardAction copyToClipboard(String text) {
        return new CopyToClipboardAction(text);
    }

    static DynamicRunCommandAction dynamicRunCommand(String command) {
        return new DynamicRunCommandAction(command);
    }

    static CustomAction custom(ResourceLocation channel, NBT payload) {
        return new CustomAction(channel, payload);
    }

    static CustomAction custom(ResourceLocation channel) {
        return custom(channel, null);
    }

    static DynamicCustomAction dynamicCustom(ResourceLocation channel, NBTCompound additions) {
        return new DynamicCustomAction(channel, additions);
    }

    static DynamicCustomAction dynamicCustom(ResourceLocation channel) {
        return new DynamicCustomAction(channel, null);
    }

    static InternalCustomAction sendResponseTo(String channel, NBTCompound additions) {
        return new InternalCustomAction(channel, additions);
    }

    static InternalCustomAction sendResponseTo(String channel) {
        return new InternalCustomAction(channel, null);
    }

}
