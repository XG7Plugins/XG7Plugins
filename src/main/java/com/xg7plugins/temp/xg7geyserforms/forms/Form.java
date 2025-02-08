package com.xg7plugins.temp.xg7geyserforms.forms;

import com.xg7plugins.boot.Plugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.FormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;

import java.util.concurrent.CompletableFuture;

@Getter
@AllArgsConstructor
public abstract class Form<F extends org.geysermc.cumulus.form.Form,R extends FormResponse> {

    protected String id;
    protected String title;
    protected Plugin plugin;

    public abstract boolean isEnabled();

    public abstract void onFinish(F form, R result, Player player);
    public abstract void onError(F form, InvalidFormResponseResult<R> result, Player player);
    public abstract void onClose(F form, Player player);

    public abstract CompletableFuture<Boolean> send(Player player);

}
