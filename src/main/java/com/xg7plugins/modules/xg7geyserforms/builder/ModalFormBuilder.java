package com.xg7plugins.modules.xg7geyserforms.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7geyserforms.forms.ModalForm;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;

public class ModalFormBuilder extends FormBuilder<
        ModalFormBuilder,
        ModalForm,
        org.geysermc.cumulus.form.ModalForm,
        ModalFormResponse
        > {

    private String content, button1, button2;

    public ModalFormBuilder(String id, Plugin plugin) {
        super(id, plugin);
    }

    public ModalFormBuilder content(String content) {
        this.content = content;
        return this;
    }

    public ModalFormBuilder button1(String button1) {
        this.button1 = button1;
        return this;
    }

    public ModalFormBuilder button2(String button2) {
        this.button2 = button2;
        return this;
    }

    @Override
    public ModalForm build() {

        if (title == null) throw new NullPointerException("title is null");
        if (content == null) throw new NullPointerException("content is null");
        if (button1 == null) throw new NullPointerException("button1 is null");
        if (button2 == null) throw new NullPointerException("button2 is null");

        return new ModalForm(plugin, id, title, content, button1, button2, buildPlaceholders) {
            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void onFinish(org.geysermc.cumulus.form.ModalForm form, ModalFormResponse result, Player player) {
                if (onFinish != null) onFinish.accept(form, result, player);
            }

            @Override
            public void onError(org.geysermc.cumulus.form.ModalForm form, InvalidFormResponseResult<ModalFormResponse> result, Player player) {
                if (onError != null) onError.accept(form, result, player);
            }

            @Override
            public void onClose(org.geysermc.cumulus.form.ModalForm form, Player player) {
                if (onClose != null) onClose.accept(form, player);
            }
        };
    }
}
