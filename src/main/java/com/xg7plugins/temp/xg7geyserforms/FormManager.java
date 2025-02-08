package com.xg7plugins.temp.xg7geyserforms;

import com.xg7plugins.temp.xg7geyserforms.forms.Form;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.HashMap;
import java.util.Map;

public class FormManager {

    private final Map<String, Form<?,?>> creators = new HashMap<>();

    public void registerForm(Form<?,?> creator) {
        if (creator == null) return;
        creators.put(creator.getId(), creator);
    }

    public void unregisterForm(String id) {
        creators.remove(id);
    }
    public boolean contaninsForm(String id) {
        return creators.containsKey(id);
    }

    public boolean sendForm(Player player, String form) {
        if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) return false;
        if (!creators.containsKey(form)) return false;

        creators.get(form).send(player);

        return true;
    }

}
