package com.xg7plugins.help.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;


@Getter
@AllArgsConstructor
public class HelpComponent {

    private static final HelpComponent EMPTY_COMPONENT = new HelpComponent("", null, null);

    @Getter
    protected HashMap<String, String> placeholders;
    private String content;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;

    public HelpComponent(String content, ClickEvent clickEvent, HoverEvent hoverEvent) {
        this.content = content;
        this.clickEvent = clickEvent;
        this.hoverEvent = hoverEvent;
        this.placeholders = new HashMap<>();
    }



    public TextComponent build(Player player) {
        TextComponent textComponent = new TextComponent(Text.format(content,XG7Plugins.getInstance()).setReplacements(placeholders).getWithPlaceholders(player));
        if (clickEvent != null) {
            ClickEvent transletedClickEvent = new ClickEvent(clickEvent.getAction(), Text.getWithPlaceholders(XG7Plugins.getInstance(), clickEvent.getValue(), player));
            textComponent.setClickEvent(transletedClickEvent);
        }
        if (hoverEvent != null) {
            textComponent.setHoverEvent(hoverEvent);
        }
        return textComponent;
    }

    public static HelpComponent empty() {
        return EMPTY_COMPONENT;
    }

}
