package com.xg7plugins.temp.xg7geyserforms.forms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.concurrent.CompletableFuture;

public abstract class ModalForm extends Form<org.geysermc.cumulus.form.ModalForm, ModalFormResponse> {

    protected final String content;
    protected final String button1;
    protected final String button2;

    public ModalForm(String id, String title, Plugin plugin, String content, String button1, String button2) {
        super(id, title, plugin);
        this.content = content;
        this.button1 = button1;
        this.button2 = button2;
    }

    @Override
    public CompletableFuture<Boolean> send(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            org.geysermc.cumulus.form.ModalForm.Builder builder = org.geysermc.cumulus.form.ModalForm.builder();

            builder.title(Text.detectLangOrText(plugin,player,title).join().getText());
            builder.content(Text.detectLangOrText(plugin,player,content).join().getText());
            builder.button1(Text.detectLangOrText(plugin,player,button1).join().getText());
            builder.button2(Text.detectLangOrText(plugin,player,button2).join().getText());

            builder.invalidResultHandler((form, response) -> XG7Plugins.taskManager().runAsyncTask(XG7Plugins.getInstance(), "menus", () -> onError(form, response, player)));
            builder.validResultHandler((form, response) -> XG7Plugins.taskManager().runAsyncTask(XG7Plugins.getInstance(),"menus", () -> onFinish(form, response, player)));
            builder.closedResultHandler((form) -> XG7Plugins.taskManager().runAsyncTask(XG7Plugins.getInstance(),"menus", () -> onClose(form, player)));

            FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());

            return true;
        }, XG7Plugins.taskManager().getAsyncExecutors().get("menus"));
    }

}
