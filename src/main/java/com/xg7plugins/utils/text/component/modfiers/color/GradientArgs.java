package com.xg7plugins.utils.text.component.modfiers.color;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

@Data
@AllArgsConstructor
public class GradientArgs {
    private final Color start;
    private final Color end;
    private final float ratio;

    public static GradientArgs of(Color start, Color end, float ratio) {
        return new GradientArgs(start, end, ratio);
    }
}
