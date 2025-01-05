package com.xg7plugins.help;

import com.xg7plugins.libs.xg7geyserforms.forms.Form;

import java.util.HashMap;

public abstract class HelpCommandForm {

    private HashMap<String, Form<?,?>> forms = new HashMap<>();

    public final void registerForm(String id, Form<?,?> form) {
        forms.put(id, form);
    }

    public Form<?,?> getForm(String id) {
        return forms.get(id);
    }

}
