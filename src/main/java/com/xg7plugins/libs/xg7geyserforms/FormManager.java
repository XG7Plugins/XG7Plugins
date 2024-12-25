package com.xg7plugins.libs.xg7geyserforms;

import com.xg7plugins.libs.xg7geyserforms.forms.Form;
import org.bukkit.entity.Player;

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

}
