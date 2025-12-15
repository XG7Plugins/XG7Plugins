package com.xg7plugins.modules.xg7dialogs;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.events.Listener;
import com.xg7plugins.modules.Module;
import com.xg7plugins.modules.xg7dialogs.listener.DialogListener;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.PluginKey;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class XG7Dialogs implements Module {

    @Getter
    private boolean enabled;

    private final Map<PluginKey, Dialog> registeredDialogs = new HashMap<>();

    private final ObjectCache<UUID, Dialog> waitingForResponse = new ObjectCache<>(
            XG7Plugins.getInstance(),
            ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().getTimeInMilliseconds("dialog-response-expires"),
            true,
            "dialog-cache",
            true,
            UUID.class,
            Dialog.class
    );

    @Override
    public void onInit() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReload() {

    }

    @Override
    public List<Listener> loadListeners() {
        return Collections.singletonList(new DialogListener());
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
        return XG7Plugins.getAPI().dialogs().getWaitingForResponse().get(uuid).join();
    }

    public static void unregisterWaitingDialog(UUID uuid) {
        XG7Plugins.getAPI().dialogs().getWaitingForResponse().remove(uuid);
    }
}
