package com.xg7plugins.help.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.event.ClickEvent;
import com.xg7plugins.utils.text.component.event.HoverEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Getter
@AllArgsConstructor
public class HelpComponent {

    private static final HelpComponent EMPTY_COMPONENT = new HelpComponent(XG7Plugins.getInstance(), "");

    protected List<Pair<String, String>> placeholders;
    private String content;
    private final Plugin plugin;

    @Setter
    private HoverEvent hoverEvent;
    @Setter
    private ClickEvent clickEvent;

    public HelpComponent(Plugin plugin, String content) {
        this.plugin = plugin;
        this.content = content;
        this.placeholders = new ArrayList<>();
    }
    @SafeVarargs
    public HelpComponent(Plugin plugin, String content, ClickEvent clickEvent, HoverEvent hoverEvent, Pair<String, String>... placeholders) {
        this.plugin = plugin;
        this.content = content;
        this.hoverEvent = hoverEvent;
        this.clickEvent = clickEvent;
        this.placeholders = Arrays.asList(placeholders);
    }

    public Component buildFor(Player player) {

        String translatedContent = Text.detectLangs(player,plugin,content).join().replaceAll(placeholders.toArray(new Pair[0])).getPlainText();

        Component component = Component.text(translatedContent).build();

        if (hoverEvent != null) component.setHoverEvent(new HoverEvent(Text.detectLangs(player,plugin,hoverEvent.content()).join().replaceAll(placeholders.toArray(new Pair[0])).getPlainText(), hoverEvent.action()));
        if (clickEvent != null) component.setClickEvent(new ClickEvent(Text.detectLangs(player,plugin,clickEvent.content()).join().replaceAll(placeholders.toArray(new Pair[0])).getPlainText(), clickEvent.action()));

        return component;
    }

    public Component build() {
        return buildFor(null);
    }

    public static HelpComponent empty() {
        return EMPTY_COMPONENT;
    }

    public static Builder of(Plugin plugin, String content) {
        return new Builder(plugin,content);
    }

    public static class Builder {

        private List<Pair<String,String>> placeholders = new ArrayList<>();

        private String content;
        private final Plugin plugin;

        private HoverEvent hoverEvent;
        private ClickEvent clickEvent;

        public Builder(Plugin plugin, String text) {
            this.plugin = plugin;
            this.content = text;
        }

        public Builder content(String text) {
            this.content = text;
            return this;
        }
        public Builder hoverEvent(HoverEvent event) {
            this.hoverEvent = event;
            return this;
        }

        public Builder clickEvent(ClickEvent event) {
            this.clickEvent = event;
            return this;
        }
        public Builder replace(String placeholder, String text) {
            this.placeholders.add(Pair.of(placeholder,text));
            return this;
        }

        public Builder replaceAll(Pair<String,String>... placeholder) {
            this.placeholders.addAll(Arrays.asList(placeholder));
            return this;
        }

        public HelpComponent build() {
            return new HelpComponent(plugin,content,clickEvent,hoverEvent,placeholders.toArray(new Pair[0]));
        }


    }

}
