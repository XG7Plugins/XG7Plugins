package com.xg7plugins.temp.xg7geyserforms.forms.customform;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.temp.xg7geyserforms.forms.Form;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public abstract class CustomForm extends Form<org.geysermc.cumulus.form.CustomForm, CustomFormResponse> {

    public CustomForm(String id, String title, Plugin plugin) {
        super(id, title, plugin);
    }

    public abstract List<IComponent> components(Player player);

    @Override
    public CompletableFuture<Boolean> send(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            org.geysermc.cumulus.form.CustomForm.Builder builder = org.geysermc.cumulus.form.CustomForm.builder();

            builder.title(Text.detectLangOrText(plugin,player,title).join().getText());

            components(player).stream().map(component -> component.build(player, plugin)).forEach(builder::component);

            builder.invalidResultHandler((form, response) -> XG7Plugins.taskManager().runAsyncTask(XG7Plugins.getInstance(),"menus", () -> onError(form, response, player)));
            builder.validResultHandler((form, response) -> XG7Plugins.taskManager().runAsyncTask(XG7Plugins.getInstance(),"menus", () -> onFinish(form, response, player)));
            builder.closedResultHandler((form) -> XG7Plugins.taskManager().runAsyncTask(XG7Plugins.getInstance(),"menus", () -> onClose(form, player)));

            FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());

            return true;
        }, XG7Plugins.taskManager().getAsyncExecutors().get("menus"));
    }
}
