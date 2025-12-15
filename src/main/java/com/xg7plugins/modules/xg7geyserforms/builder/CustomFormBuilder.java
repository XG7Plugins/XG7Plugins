package com.xg7plugins.modules.xg7geyserforms.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7geyserforms.forms.customform.CustomForm;
import com.xg7plugins.modules.xg7geyserforms.forms.customform.IComponent;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;

import java.util.ArrayList;
import java.util.List;

public class CustomFormBuilder extends FormBuilder<
        CustomFormBuilder,
        CustomForm,
        org.geysermc.cumulus.form.CustomForm,
        CustomFormResponse
        > {

    private final List<IComponent> components = new ArrayList<>();

    public CustomFormBuilder(String id, Plugin plugin) {
        super(id, plugin);
    }

    public CustomFormBuilder addComponent(IComponent component) {
        components.add(component);
        return this;
    }

    public CustomFormBuilder addComponents(IComponent... components) {
        for (IComponent component : components) this.addComponent(component);
        return this;
    }

    public CustomFormBuilder addComponents(List<IComponent> components) {
        for (IComponent component : components) this.addComponent(component);
        return this;
    }

    @Override
    public CustomForm build() {

        if (title == null) throw new NullPointerException("title is null");

        return new CustomForm(id, title, plugin, buildPlaceholders) {
            @Override
            public List<IComponent> components(Player player) {
                return components;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void onFinish(org.geysermc.cumulus.form.CustomForm form, CustomFormResponse result, Player player) {
                if (onFinish != null) onFinish.accept(form,result,player);
            }

            @Override
            public void onError(org.geysermc.cumulus.form.CustomForm form, InvalidFormResponseResult<CustomFormResponse> result, Player player) {
                if (onError != null) onError.accept(form,result,player);
            }

            @Override
            public void onClose(org.geysermc.cumulus.form.CustomForm form, Player player) {
                if (onClose != null) onClose.accept(form,player);
            }
        };
    }
}
