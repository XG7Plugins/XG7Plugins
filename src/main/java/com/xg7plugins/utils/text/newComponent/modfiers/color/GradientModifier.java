package com.xg7plugins.utils.text.newComponent.modfiers.color;

import com.xg7plugins.utils.text.newComponent.Component;
import com.xg7plugins.utils.text.newComponent.modfiers.TextModifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GradientModifier implements TextModifier {

    private final List<Color> gradients;
    private final GradientStyle style;

    public GradientModifier(Color... colors) {
        this(new ArrayList<>(), GradientStyle.LINEAR);
        for (Color color : colors) gradients.add(color);
        if (gradients.size() < 2) throw new IllegalArgumentException("Gradients must have at least 2 colors");
    }
    public GradientModifier(GradientStyle style, Color... colors) {
        this(new ArrayList<>(), style);
        for (Color color : colors) gradients.add(color);
    }
    public GradientModifier(List<Color> colors) {
        this(colors, GradientStyle.LINEAR);
    }
    public GradientModifier(GradientStyle style, List<Color> colors) {
        this(colors, style);
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
        return new GradientModifier(style, colors);
    }

}
