package com.xg7plugins.modules.xg7geyserforms.forms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.tasks.AsyncTask;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.concurrent.CompletableFuture;

public abstract class ModalForm extends Form<org.geysermc.cumulus.form.ModalForm, ModalFormResponse> {

    protected final String content;
    protected final String button1;
    protected final String button2;

    public ModalForm(Plugin plugin, String id, String title, String content, String button1, String button2) {
        super(id, title, plugin);
        this.content = content;
        this.button1 = button1;
        this.button2 = button2;
    }

    @Override
    public CompletableFuture<Boolean> send(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            org.geysermc.cumulus.form.ModalForm.Builder builder = org.geysermc.cumulus.form.ModalForm.builder();

            builder.title(Text.detectLangs(player, plugin,title).join().getText());
            builder.content(Text.detectLangs(player, plugin,content).join().getText());
            builder.button1(Text.detectLangs(player, plugin,button1).join().getText());
            builder.button2(Text.detectLangs(player, plugin,button2).join().getText());

            builder.invalidResultHandler((form, response) -> AsyncTask.of(XG7Plugins.getInstance(),"menus", () -> onError(form, response, player)));
            builder.validResultHandler((form, response) -> onFinish(form, response, player));
            builder.closedResultHandler((form) -> onClose(form, player));

            FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());

            return true;
        }, XG7PluginsAPI.taskManager().getExecutor("menus"));
    }

}
