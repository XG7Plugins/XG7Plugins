package com.xg7plugins.modules.xg7dialogs;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.Module;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.PluginKey;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class XG7Dialogs implements Module {

    @Getter
    private boolean enabled;

    private final Map<PluginKey, Dialog> registeredDialogs = new HashMap<>();

    private final Map<UUID, Dialog> waitingForResponse = new HashMap<>();

    @Override
    public void onInit() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReload() {

    }

    public void registerDialogs(Dialog... dialogs) {
        for (Dialog dialog : dialogs) {
            this.registeredDialogs.put(PluginKey.of(dialog.getPlugin(), dialog.getId()), dialog);
        }
    }

    public Dialog getDialog(PluginKey pluginKey) {
        return registeredDialogs.get(pluginKey);
    }

    @Override
    public String getName() {
        return "XG7Dialogs";
    }

    @Override
    public boolean canBeEnabled() {
        return MinecraftServerVersion.isNewerOrEqual(ServerVersion.V_1_21_6);
    }
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static void registerWaitingDialog(Player player, Dialog dialog) {
        XG7Plugins.getAPI().dialogs().getWaitingForResponse().put(player.getUniqueId(), dialog);
    }

    public static Dialog getWaitingDialog(UUID uuid) {
        return XG7Plugins.getAPI().dialogs().getWaitingForResponse().get(uuid);
    }

    public static void unregisterWaitingDialog(UUID uuid) {
        XG7Plugins.getAPI().dialogs().getWaitingForResponse().remove(uuid);
    }
}
