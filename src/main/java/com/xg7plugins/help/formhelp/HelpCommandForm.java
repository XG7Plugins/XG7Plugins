package com.xg7plugins.help.formhelp;

import com.xg7plugins.libs.xg7geyserforms.forms.Form;

import java.util.ArrayList;
import java.util.HashMap;

public class HelpCommandForm {

    private final HashMap<String, Form<?,?>> forms = new HashMap<>();

    public HelpCommandForm(Form<?,?> index) {
        this.forms.put("index", index);
        this.forms.put("commands", new CommandForm(index.getPlugin(), new ArrayList<>(index.getPlugin().getCommandManager().getCommands().values()), null, null, this));

    }

    public final void registerForm(String id, Form<?,?> form) {
        forms.put(id, form);
    }

    public final Form<?,?> getForm(String id) {
        return forms.get(id);
    }



}
