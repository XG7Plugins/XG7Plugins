package com.xg7plugins.modules.xg7geyserforms.forms;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;

public abstract class SimpleForm extends Form<org.geysermc.cumulus.form.SimpleForm, SimpleFormResponse> {

    public SimpleForm(String id, String title, Plugin plugin, List<Pair<String, String>> buildPlaceholders) {
        super(id, title, plugin, buildPlaceholders);
    }

    public abstract String content(Player player);

    public abstract List<ButtonComponent> buttons(Player player);

    @Override
    public boolean send(Player player) {

        org.geysermc.cumulus.form.SimpleForm.Builder builder = org.geysermc.cumulus.form.SimpleForm.builder();

        builder.title(Text.detectLangs(player, plugin, title).replaceAll(buildPlaceholders).getText());
        builder.content(Text.detectLangs(player, plugin, content(player)).replaceAll(buildPlaceholders).getText());

        buttons(player).forEach(btn -> {

            if (btn.image() != null) {

                builder.button(ButtonComponent.of(Text.detectLangs(player, plugin, btn.text()).replaceAll(buildPlaceholders).getText(), btn.image()));
                return;

            }

            builder.button(ButtonComponent.of(Text.detectLangs(player, plugin, btn.text()).replaceAll(buildPlaceholders).getText()));

        });

        builder.invalidResultHandler((form, response) -> onError(form, response, player));
        builder.validResultHandler((form, response) -> onFinish(form, response, player));
        builder.closedResultHandler((form) -> onClose(form, player));

        FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());

        return true;
    }

}
