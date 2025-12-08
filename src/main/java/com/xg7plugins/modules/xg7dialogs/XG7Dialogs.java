package com.xg7plugins.modules.xg7dialogs;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.modules.Module;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import lombok.Getter;

import java.util.Map;

public class XG7Dialogs implements Module {

    @Getter
    private boolean enabled;

    private Map<String, Dialog> registeredDialogs;


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
}
