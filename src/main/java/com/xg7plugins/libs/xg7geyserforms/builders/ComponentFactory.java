package com.xg7plugins.libs.xg7geyserforms.builders;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.*;

import java.util.List;
import java.util.stream.Collectors;

public class ComponentFactory {

    public static IComponent.Input input(String label, String placeholder, String defText) {
        return new IComponent.Input(label, placeholder, defText);
    }
    public static IComponent.DropDown dropDown(String label, List<String> options, int defOption) {
        return new IComponent.DropDown(label, options, defOption);
    }
    public static IComponent.Slider slider(String label, int min, int max, int step, int defaultVal) {
        return new IComponent.Slider(label, min, max, step, defaultVal);
    }
    public static IComponent.Toggle toggle(String text, boolean defaultVal) {
        return new IComponent.Toggle(text, defaultVal);
    }


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
}
