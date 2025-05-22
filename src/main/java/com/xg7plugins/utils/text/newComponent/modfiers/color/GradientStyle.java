package com.xg7plugins.utils.text.newComponent.modfiers.color;

import java.awt.*;
import java.util.function.Function;

public enum GradientStyle {

    /**
     * Gradiente linear padrão de uma cor para outra
     */
    LINEAR(args -> {
        int red = (int) (args.getStart().getRed() * (1 - args.getRatio()) + args.getEnd().getRed() * args.getRatio());
        int green = (int) (args.getStart().getGreen() * (1 - args.getRatio()) + args.getEnd().getGreen() * args.getRatio());
        int blue = (int) (args.getStart().getBlue() * (1 - args.getRatio()) + args.getEnd().getBlue() * args.getRatio());
        return new Color(red, green, blue);
    }),

    /**
     * Gradiente com efeito arco-íris que passa por múltiplas cores
     */
    RAINBOW(args -> {
        float[] startHSB = Color.RGBtoHSB(args.getStart().getRed(), args.getStart().getGreen(), args.getStart().getBlue(), null);
        float[] endHSB = Color.RGBtoHSB(args.getEnd().getRed(), args.getEnd().getGreen(), args.getEnd().getBlue(), null);

        float hue = (startHSB[0] + (args.getRatio() * 1.0f)) % 1.0f;
        float saturation = Math.max(startHSB[1], endHSB[1]);
        float brightness = Math.max(startHSB[2], endHSB[2]);

        return Color.getHSBColor(hue, saturation, brightness);
    }),

    /**
     * Gradiente em forma de onda, avançando e retrocedendo entre as cores
     */
    WAVE(args -> {
        float wave = (float) (Math.sin(args.getRatio() * Math.PI * 2) * 0.5 + 0.5);

        int red = (int) (args.getStart().getRed() * (1 - wave) + args.getEnd().getRed() * wave);
        int green = (int) (args.getStart().getGreen() * (1 - wave) + args.getEnd().getGreen() * wave);
        int blue = (int) (args.getStart().getBlue() * (1 - wave) + args.getEnd().getBlue() * wave);

        return new Color(red, green, blue);
    }),

    /**
     * Gradiente que pulsa, alternando entre as cores iniciais e finais
     */
    PULSE(args -> {
        float pulse = (float) Math.pow(Math.sin(args.getRatio() * Math.PI), 2);

        int red = (int) (args.getStart().getRed() * (1 - pulse) + args.getEnd().getRed() * pulse);
        int green = (int) (args.getStart().getGreen() * (1 - pulse) + args.getEnd().getGreen() * pulse);
        int blue = (int) (args.getStart().getBlue() * (1 - pulse) + args.getEnd().getBlue() * pulse);

        return new Color(red, green, blue);
    }),

    /**
     * Gradiente que aplica uma função exponencial à transição de cores
     */
    EXPONENTIAL(args -> {
        // Aplica uma função exponencial à transição
        float expRatio = (float) Math.pow(args.getRatio(), 2);

        int red = (int) (args.getStart().getRed() * (1 - expRatio) + args.getEnd().getRed() * expRatio);
        int green = (int) (args.getStart().getGreen() * (1 - expRatio) + args.getEnd().getGreen() * expRatio);
        int blue = (int) (args.getStart().getBlue() * (1 - expRatio) + args.getEnd().getBlue() * expRatio);

        return new Color(red, green, blue);
    });

    private Function<GradientArgs, Color> interpolator;

    GradientStyle(Function<GradientArgs, Color> interpolator) {
        this.interpolator = interpolator;
    }

    public Color apply(GradientArgs args) {
        return interpolator.apply(args);
    }

}

