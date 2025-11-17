package com.xg7plugins.modules.xg7geyserforms;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.modules.Module;
import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.HashMap;

@Getter
public class XG7GeyserForms implements Module {

    private boolean enabled;

    private final HashMap<String, Form<?,?>> forms = new HashMap<>();

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
        return "XG7GeyserForms";
    }

    public void registerForm(Form<?,?> creator) {
        if (creator == null) return;
        forms.put(creator.getId(), creator);
    }

    public void unregisterForm(String id) {
        forms.remove(id);
    }
    public boolean containsForm(String id) {
        return forms.containsKey(id);
    }

    public boolean sendForm(Player player, String formId) {
        if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) return false;
        if (!forms.containsKey(formId)) return false;

        forms.get(formId).send(player);

        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean canBeEnabled() {
        return XG7PluginsAPI.isGeyserFormsEnabled();
    }
}