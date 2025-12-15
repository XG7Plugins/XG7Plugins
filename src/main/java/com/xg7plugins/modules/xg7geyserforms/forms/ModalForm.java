package com.xg7plugins.modules.xg7geyserforms.forms;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.tasks.AsyncTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;

public abstract class ModalForm extends Form<org.geysermc.cumulus.form.ModalForm, ModalFormResponse> {

    protected final String content;
    protected final String button1;
    protected final String button2;

    public ModalForm(Plugin plugin, String id, String title, String content, String button1, String button2, List<Pair<String, String>>  buildPlaceholders) {
        super(id, title, plugin, buildPlaceholders);
        this.content = content;
        this.button1 = button1;
        this.button2 = button2;
    }

    @Override
    public boolean send(Player player) {
        org.geysermc.cumulus.form.ModalForm.Builder builder = org.geysermc.cumulus.form.ModalForm.builder();

        builder.title(Text.detectLangs(player, plugin, title).replaceAll(buildPlaceholders).getText());
        builder.button1(Text.detectLangs(player, plugin, button1).replaceAll(buildPlaceholders).getText());
        builder.button2(Text.detectLangs(player, plugin, button2).replaceAll(buildPlaceholders).getText());
        builder.content(Text.detectLangs(player, plugin, content).replaceAll(buildPlaceholders).getText());

        builder.invalidResultHandler((form, response) -> onError(form, response, player));
        builder.validResultHandler((form, response) -> onFinish(form, response, player));
        builder.closedResultHandler((form) -> onClose(form, player));

        FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());

        return true;
    }

}
