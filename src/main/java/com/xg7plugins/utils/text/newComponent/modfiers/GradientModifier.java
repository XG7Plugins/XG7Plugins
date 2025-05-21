package com.xg7plugins.utils.text.newComponent.modfiers;

import com.xg7plugins.utils.text.newComponent.Component;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

@Data
@AllArgsConstructor
public class GradientModifier implements TextModifier {

    private final Color startColor;
    private final Color endColor;

    @Override
    public void apply(Component component) {
        String content = component.getContent();
        StringBuilder result = new StringBuilder();

        int length = content.length();
        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            Color currentColor = interpolateColor(startColor, endColor, ratio);
            result.append(String.format("§x§%1$s§%2$s§%3$s§%4$s§%5$s§%6$s",
                            String.format("%02x", currentColor.getRed()),
                            String.format("%02x", currentColor.getGreen()),
                            String.format("%02x", currentColor.getBlue()))
                    .charAt(i));
        }

        component.setContent(result.toString());
    }

    private Color interpolateColor(Color color1, Color color2, float ratio) {
        int red = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
        int green = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
        int blue = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
        return new Color(red, green, blue);
    }
}
