package com.xg7plugins.temp.xg7geyserforms.forms.customform;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.*;

import java.util.List;
import java.util.stream.Collectors;

public interface IComponent {
    Component build(Player player, Plugin plugin);

    @AllArgsConstructor
    @Getter
    class Input implements IComponent {
        private final String label;
        private final String placeholder;
        private final String defText;

        @Override
        public Component build(Player player, Plugin plugin) {
            return InputComponent.of(
                    Text.detectLangOrText(plugin,player,label).join().getText(),
                    Text.detectLangOrText(plugin,player,placeholder).join().getText(),
                    Text.detectLangOrText(plugin,player,defText).join().getText()
            );
        }
    }

    @AllArgsConstructor
    @Getter
    class DropDown implements IComponent {
        private final String label;
        private final List<String> options;
        private final int defOption;

        @Override
        public Component build(Player player, Plugin plugin) {


            return DropdownComponent.of(
                    Text.detectLangOrText(plugin,player,label).join().getText(),
                    options.stream().map(option -> Text.detectLangOrText(plugin,player,option).join().getText()).collect(Collectors.toList()),
                    defOption
            );
        }
    }

    @AllArgsConstructor
    @Getter
    class Slider implements IComponent {
        private final String label;
        private final int min;
        private final int max;
        private final int step;
        private final int defaultVal;

        @Override
        public Component build(Player player, Plugin plugin) {
            return SliderComponent.of(
                    Text.detectLangOrText(plugin,player,label).join().getText(),
                    min,
                    max,
                    step,
                    defaultVal
            );
        }
    }

    @AllArgsConstructor
    @Getter
    class Toggle implements IComponent {
        private final String text;
        private final boolean defaultVal;

        @Override
        public Component build(Player player, Plugin plugin) {
            return ToggleComponent.of(
                    Text.detectLangOrText(plugin,player,text).join().getText(),
                    defaultVal
            );
        }
    }
}
