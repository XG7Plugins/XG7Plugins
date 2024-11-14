package com.xg7plugins.libs.xg7geyserforms.builders;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.response.FormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


public abstract class FormCreator<FC extends FormCreator<FC>> {

    @Getter
    protected String id;
    protected String title;
    protected final Plugin plugin;

    protected BiConsumer <Form,FormResponse> finish;
    protected BiConsumer<Form,InvalidFormResponseResult<? extends FormResponse>> error;
    protected Consumer<Form> close;

    public FormCreator(String id, Plugin plugin) {
        this.id = id;
        this.plugin = plugin;
        XG7Plugins.getInstance().getFormManager().registerCreator(this);

    }

    public FC title(String title) {
        this.title = title;
        return (FC) this;
    }

    public <F extends Form, FR extends FormResponse> FC onFinish(BiConsumer<F, FR> consumer) {
        this.finish = (BiConsumer<Form, FormResponse>) consumer;
        return (FC) this;
    }
    public <F extends Form, FR extends InvalidFormResponseResult<? extends FormResponse>> FC onError(BiConsumer<F, FR> consumer) {
        this.error = (BiConsumer<Form, InvalidFormResponseResult<? extends FormResponse>>) consumer;
        return (FC) this;
    }
    public <F extends Form> FC onClose(Consumer<F> consumer) {
        this.close = (Consumer<Form>) consumer;
        return (FC) this;
    }
    public static ModalFormCreator modal(String id, Plugin plugin) {
        return new ModalFormCreator(id, plugin);
    }
    public static SimpleFormCreator simple(String id, Plugin plugin) {
        return new SimpleFormCreator(id, plugin);
    }
    public static CustomFormCreator custom(String id, Plugin plugin) {
        return new CustomFormCreator(id, plugin);
    }


    public abstract <F extends Form> F build(Player player);
}
