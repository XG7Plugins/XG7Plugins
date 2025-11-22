package com.xg7plugins.modules.xg7geyserforms.forms.customform;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;

@Getter
public abstract class CustomForm extends Form<org.geysermc.cumulus.form.CustomForm, CustomFormResponse> {

    public CustomForm(String id, String title, Plugin plugin) {
        super(id, title, plugin);
    }

    public abstract List<IComponent> components(Player player);

    @Override
    public boolean send(Player player) {
        org.geysermc.cumulus.form.CustomForm.Builder builder = org.geysermc.cumulus.form.CustomForm.builder();

        builder.title(Text.detectLangs(player, plugin, title).join().getText());

        components(player).stream().map(component -> component.build(player, plugin)).forEach(builder::component);

        builder.invalidResultHandler((form, response) -> onError(form, response, player));
        builder.validResultHandler((form, response) -> onFinish(form, response, player));
        builder.closedResultHandler((form) -> onClose(form, player));

        FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());

        return true;
    }
}
