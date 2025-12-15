package com.xg7plugins.modules.xg7geyserforms.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import com.xg7plugins.utils.Pair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.FormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.event.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
@Getter
public abstract class FormBuilder<
        B extends FormBuilder<B, F, G, R>,
        F extends Form<G, R>,
        G extends org.geysermc.cumulus.form.Form,
        R extends FormResponse
        >
{

    protected final String id;
    protected final Plugin plugin;

    protected String title;

    protected TriConsumer<G, R, Player> onFinish;
    protected TriConsumer<G, InvalidFormResponseResult<R>, Player> onError;
    protected BiConsumer<G, Player> onClose;

    protected final List<Pair<String, String>> buildPlaceholders = new ArrayList<>();

    public B title(String title) {
        this.title = title;
        return (B) this;
    }

    public B onFinish(TriConsumer<G, R, Player> onFinish) {
        this.onFinish = onFinish;
        return (B) this;
    }

    public B onError(TriConsumer<G, InvalidFormResponseResult<R>, Player> onError) {
        this.onError = onError;
        return (B) this;
    }

    public B onClose(BiConsumer<G, Player> onClose) {
        this.onClose = onClose;
        return (B) this;
    }

    public B addBuilderPlaceholders(List<Pair<String, String>> placeholders) {
        this.buildPlaceholders.addAll(placeholders);
        return (B) this;
    }

    public abstract F build();

    public static CustomFormBuilder custom(String id, Plugin plugin) {
        return new CustomFormBuilder(id, plugin);
    }

    public static ModalFormBuilder modal(String id, Plugin plugin) {
        return new ModalFormBuilder(id, plugin);
    }

    public static SimpleFormBuilder simple(String id, Plugin plugin) {
        return new SimpleFormBuilder(id, plugin);
    }

}
