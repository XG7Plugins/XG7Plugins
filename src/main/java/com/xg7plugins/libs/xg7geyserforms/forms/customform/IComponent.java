package com.xg7plugins.libs.xg7geyserforms.forms.customform;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.*;
import org.geysermc.cumulus.util.FormImage;

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
                    Text.format(label, plugin).getWithPlaceholders(player),
                    Text.format(placeholder, plugin).getWithPlaceholders(player),
                    Text.format(defText, plugin).getWithPlaceholders(player)
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
                    Text.format(label, plugin).getWithPlaceholders(player),
                    options.stream().map(option -> Text.format(option, plugin).getWithPlaceholders(player)).collect(Collectors.toList()),
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
                    Text.format(label, plugin).getWithPlaceholders(player),
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
                    Text.format(text, plugin).getWithPlaceholders(player),
                    defaultVal
            );
        }
    }
}
