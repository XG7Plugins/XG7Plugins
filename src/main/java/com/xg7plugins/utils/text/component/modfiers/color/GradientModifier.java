package com.xg7plugins.utils.text.component.modfiers.color;

import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.modfiers.TextModifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Data
public class GradientModifier implements TextModifier {

    private final List<Color> gradients;
    private final GradientStyle style;

    public GradientModifier(List<Color> gradients, GradientStyle style) {
        this.gradients = gradients;
        this.style = style;
    }
    public GradientModifier(GradientStyle style, Color... colors) {
        this(Arrays.asList(colors), style);
    }
    public GradientModifier(List<Color> gradients) {
        this(gradients, GradientStyle.LINEAR);
    }
    public GradientModifier(Color... colors) {
        this(Arrays.asList(colors));
    }

    @Override
    public void apply(Component component) {
        String content = component.getContent();
        StringBuilder result = new StringBuilder();

        int length = content.length();

        // Verificar se temos pelo menos 2 cores
        if (gradients.size() < 2) {
            component.setContent(content); // Retorna sem modificar se não tiver cores suficientes
            return;
        }

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            Color currentColor = calculateMultiColorGradient(ratio);

            result.append(String.format("§x§%1$s§%2$s§%3$s§%4$s§%5$s§%6$s%7$s",
                    formatHex(currentColor.getRed()),
                    formatHex(currentColor.getGreen()),
                    formatHex(currentColor.getBlue()),
                    content.charAt(i)));
        }

        component.setContent(result.toString());
    }

    /**
     * Calcula a cor interpolada entre múltiplas cores baseada no ratio
     * @param ratio Valor entre 0.0 e 1.0 representando a posição no gradiente
     * @return Cor interpolada
     */
    private Color calculateMultiColorGradient(float ratio) {
        // Garantir que ratio está entre 0 e 1
        ratio = Math.max(0, Math.min(1, ratio));

        // Calcular em qual segmento do gradiente estamos
        int segments = gradients.size() - 1;
        float segmentSize = 1.0f / segments;
        int currentSegment = Math.min((int) (ratio / segmentSize), segments - 1);

        // Calcular a posição dentro do segmento atual
        float segmentRatio = (ratio - (currentSegment * segmentSize)) / segmentSize;

        // Obter as cores do segmento atual
        Color startColor = gradients.get(currentSegment);
        Color endColor = gradients.get(currentSegment + 1);

        // Interpolar entre as duas cores
        return style.apply(GradientArgs.of(startColor, endColor, segmentRatio));
    }

    /**
     * Formata um valor int para hexadecimal de 2 dígitos
     * @param value Valor a ser formatado
     * @return String hexadecimal de 2 dígitos
     */
    private String formatHex(int value) {
        return String.format("%02x", value);
    }

    public static GradientModifier of(GradientStyle style, Color... colors) {
        return new GradientModifier(style, colors);
    }

    public static GradientModifier of(Color... colors) {
        return new GradientModifier(colors);
    }

    public static GradientModifier of(List<Color> colors) {
        return new GradientModifier(colors);
    }

    public static GradientModifier of(List<Color> colors, GradientStyle style) {
        return new GradientModifier(colors, style);
    }

    public static GradientModifier of(GradientStyle style, List<Color> colors) {
        return new GradientModifier(colors, style);
    }

    @Override
    public String serialize() {

        StringBuilder builder = new StringBuilder();

        builder.append("gradient");

        if (!gradients.isEmpty() || style != GradientStyle.LINEAR) builder.append(":");
        if (style != GradientStyle.LINEAR) builder.append(style.name()).append(":");

        for (Color color : gradients) builder.append("#").append(Integer.toHexString(color.getRGB()).substring(2).toUpperCase()).append(":");

        if (builder.charAt(builder.length() - 1) == ':') builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

}
