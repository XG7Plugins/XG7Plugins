package com.xg7plugins.libs.xg7geyserforms.builders;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.ModalForm;


public class ModalFormCreator extends FormCreator<ModalFormCreator> {

    private String content;
    private String button1;
    private String button2;

    public ModalFormCreator(String id, Plugin plugin) {
        super(id,plugin);
    }

    public ModalFormCreator content(String content) {
        this.content = content;
        return this;
    }

    public ModalFormCreator button1(String button1) {
        this.button1 = button1;
        return this;
    }
    public ModalFormCreator button2(String button2) {
        this.button2 = button2;
        return this;
    }

    @Override
    public ModalForm build(Player player) {

        ModalForm.Builder builder = ModalForm.builder();

        if (title == null || content == null) {
            throw new IllegalStateException("Title and content must be set");
        }

        builder.title(Text.format(title,plugin).getWithPlaceholders(player));
        builder.content(Text.format(content,plugin).getWithPlaceholders(player));
        builder.button1(Text.format(button1,plugin).getWithPlaceholders(player));
        builder.button2(Text.format(button2,plugin).getWithPlaceholders(player));

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
