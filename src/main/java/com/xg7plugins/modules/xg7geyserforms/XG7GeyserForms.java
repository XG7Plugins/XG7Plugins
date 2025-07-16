package com.xg7plugins.modules.xg7geyserforms;

import com.xg7plugins.modules.Module;
import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.HashMap;

@Getter
public class XG7GeyserForms implements Module {

    private final HashMap<String, Form<?,?>> forms = new HashMap<>();
    @Getter
    private static XG7GeyserForms instance;

    @Override
    public void onInit() {
        instance = this;
    }

    @Override
    public void onDisable() {

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
}