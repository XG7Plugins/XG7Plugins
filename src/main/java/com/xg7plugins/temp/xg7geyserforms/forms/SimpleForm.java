package com.xg7plugins.temp.xg7geyserforms.forms;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class SimpleForm extends Form<org.geysermc.cumulus.form.SimpleForm, SimpleFormResponse> {


    public SimpleForm(String id, String title, Plugin plugin) {
        super(id, title, plugin);
    }

    public abstract String content(Player player);

    public abstract List<ButtonComponent> buttons(Player player);

    @Override
    public CompletableFuture<Boolean> send(Player player) {
        return CompletableFuture.supplyAsync(() -> {

            org.geysermc.cumulus.form.SimpleForm.Builder builder = org.geysermc.cumulus.form.SimpleForm.builder();

            builder.title(Text.detectLangOrText(plugin,player,title).join().getText());
            builder.content(Text.detectLangOrText(plugin,player,content(player)).join().getText());

            buttons(player).forEach(builder::button);

            builder.invalidResultHandler((form, response) -> XG7Plugins.taskManager().runAsyncTask(XG7Plugins.getInstance(), "menus", () -> onError(form, response, player)));
            builder.validResultHandler((form, response) -> XG7Plugins.taskManager().runAsyncTask(XG7Plugins.getInstance(),"menus", () -> onFinish(form, response, player)));
            builder.closedResultHandler((form) -> XG7Plugins.taskManager().runAsyncTask(XG7Plugins.getInstance(), "menus", () -> onClose(form, player)));

            FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());

            return true;
        }, XG7Plugins.taskManager().getAsyncExecutors().get("menus"));
    }

}
