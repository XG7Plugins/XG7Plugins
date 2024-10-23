package com.xg7plugins.libs.xg7geyserforms.builders;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.List;

public class SimpleFormCreator extends FormCreator<SimpleFormCreator> {

    private List<ButtonComponent> buttons = new ArrayList<>();
    private String content;

    public SimpleFormCreator content(String content) {
        this.content = content;
        return this;
    }
    public SimpleFormCreator(String id, Plugin plugin) {
        super(id, plugin);
    }

    public SimpleFormCreator addButton(ButtonComponent button) {
        buttons.add(button);
        return this;
    }

    @Override
    public SimpleForm build(Player player) {

        if (title == null || content == null) {
            throw new IllegalStateException("Title and content must be set");
        }

        SimpleForm.Builder builder = SimpleForm.builder();

        builder.title(Text.format(title,plugin).getWithPlaceholders(player));
        builder.content(Text.format(content,plugin).getWithPlaceholders(player));

        buttons.forEach(builder::button);

        if (error != null) {
            builder.invalidResultHandler((form, response) -> XG7Plugins.getInstance().getTaskManager().runTask(() -> error.accept(form, response)));
        }
        if (finish != null) {
            builder.validResultHandler((form, response) -> XG7Plugins.getInstance().getTaskManager().runTask(() -> finish.accept(form, response)));
        }
        if (close != null) {
            builder.closedResultHandler((form) -> XG7Plugins.getInstance().getTaskManager().runTask(() -> close.accept(form)));
        }
        return builder.build();
    }
}
