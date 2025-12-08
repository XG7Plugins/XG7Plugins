package com.xg7plugins.modules.xg7dialogs.dialogs;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerShowDialog;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogInput;
import com.xg7plugins.utils.text.Text;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@Data
public abstract class Dialog {

    protected final Plugin plugin;

    protected final String id;
    protected final String title;
    protected final boolean canCloseWithEscape;
    protected final List<DialogBodyElement> dialogBodyElements;
    protected final List<DialogInput> dialogInputs;
    protected final ActionType afterResponse;

    public CommonDialogData buildCommonData(Player player) {
        return new CommonDialogData(
                Text.detectLangs(player, plugin, title).join().toAdventureComponent(),
                Text.detectLangs(player, plugin, title).join().toAdventureComponent(),
                canCloseWithEscape,
                false,
                afterResponse.build(),
                dialogBodyElements.stream().map(element -> element.build(this, player)).collect(Collectors.toList()),
                dialogInputs.stream().map(input -> input.build(this, player)).collect(Collectors.toList())
        );
    }

    public String getNamespace() {
        return (plugin.getName() + "_" + id).toLowerCase();
    }

    public abstract com.github.retrooper.packetevents.protocol.dialog.Dialog build(Player player);


    public enum ActionType {
        CLOSE,
        NONE,
        WAIT_FOR_RESPONSE;

        public DialogAction build() {
            switch (this) {
                case CLOSE:
                    return DialogAction.CLOSE;
                case WAIT_FOR_RESPONSE:
                    return DialogAction.WAIT_FOR_RESPONSE;
                default:
                    return DialogAction.NONE;
            }
        }
    }

    public void send(Player player) {
        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_21_6)) return;

        WrapperPlayServerShowDialog packet = new WrapperPlayServerShowDialog(build(player));

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }






}
