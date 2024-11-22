package com.xg7plugins.libs.xg7geyserforms.builders;

import com.xg7plugins.Plugin;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomFormCreator extends FormCreator<CustomForm,CustomFormCreator> {

    private List<ComponentFactory.IComponent> components = new ArrayList<>();

    public CustomFormCreator(String id, Plugin plugin) {
        super(id, plugin);
    }

    public CustomFormCreator addComponent(ComponentFactory.IComponent component) {
        components.add(component);
        return this;
    }


    @Override
    public CustomForm build(Object... args) {
        CustomForm.Builder builder = CustomForm.builder();

        components.stream().map(component -> component.build((Player) args[0], plugin)).forEach(builder::component);

        return builder.build();
    }

}
