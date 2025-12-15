package com.xg7plugins.config.editor;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Data
@RequiredArgsConstructor
public abstract class InGameEditor {

    protected ConfigSection currentSection;
    protected boolean saved = true;
    protected final Player player;

    public abstract void sendPage(ConfigSection configSection);

    public abstract void sendEditRequest(String key, Class<?> type);

    public abstract void sendAddRequest();

    public void removeKey(String key) {
        currentSection.remove(key);
        saved = false;
    }

    public void save() {
        currentSection.getFile().save();
        saved = true;
        Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.saved");
    }



}
