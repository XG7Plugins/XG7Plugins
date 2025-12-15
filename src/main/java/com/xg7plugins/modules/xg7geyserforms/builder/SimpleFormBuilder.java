package com.xg7plugins.modules.xg7geyserforms.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SimpleFormBuilder extends FormBuilder<
        SimpleFormBuilder,
        SimpleForm,
        org.geysermc.cumulus.form.SimpleForm,
        SimpleFormResponse
        > {

    private String content;

    private Function<Player, List<ButtonComponent>> buildButtons;

    public SimpleFormBuilder(String id, Plugin plugin) {
        super(id, plugin);
    }

    public SimpleFormBuilder content(String content) {
        this.content = content;
        return this;
    }

    public SimpleFormBuilder components(Function<Player, List<ButtonComponent>> buildButtons) {
        this.buildButtons = buildButtons;
        return this;
    }

    @Override
    public SimpleForm build() {

        if (content == null) {
            throw new NullPointerException("content is null");
        }
        if (title == null) {
            throw new NullPointerException("title is null");
        }

        return new SimpleForm(id, title, plugin, buildPlaceholders) {
            @Override
            public String content(Player player) {
                return content;
            }

            @Override
            public List<ButtonComponent> buttons(Player player) {
                return buildButtons != null ? buildButtons.apply(player) : new ArrayList<>();
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {
                if (onFinish != null) onFinish.accept(form, result, player);
            }

            @Override
            public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {
                if (onError != null) onError.accept(form, result, player);
            }

            @Override
            public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {
                if (onClose != null) onClose.accept(form, player);
            }
        };
    }
}
