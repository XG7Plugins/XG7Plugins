package com.xg7plugins.help.form;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.help.HelpComponent;
import com.xg7plugins.help.form.command.CommandForm;
import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class HelpForm implements HelpComponent {

    private final HashMap<String, Form<?,?>> forms = new HashMap<>();

    public HelpForm(Form<?,?> index) {
        this.forms.put("index", index);
        this.forms.put("commands", new CommandForm(new ArrayList<>(XG7PluginsAPI.rootCommandNodesOf(index.getPlugin())), null, null, this));

    }

    public final void registerForm(String id, Form<?,?> form) {
        forms.put(id, form);
    }

    public final Form<?,?> getForm(String id) {
        return forms.get(id);
    }


    @Override
    public void send(CommandSender sender) {
        forms.get("index").send((Player) sender);
    }
}
