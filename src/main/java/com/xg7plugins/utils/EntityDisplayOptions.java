package com.xg7plugins.utils;

import com.github.retrooper.packetevents.util.Vector3f;
import lombok.Data;

import java.awt.*;

@Data
public class EntityDisplayOptions {

    private final Vector3f scale;
    private final float rotationX;
    private final float rotationY;
    private final boolean background;
    private final Color backgroundColor;
    private final boolean shadow;
    private final boolean seeThrough;
    private final EntityDisplayOptions.Billboard billboard;
    private final EntityDisplayOptions.Alignment alignment;

    public static EntityDisplayOptions of(
            Vector3f scale,
            float rotationX,
            float rotationY,
            boolean background,
            Color backgroundColor,
            boolean shadow,
            boolean seeThrough,
            Billboard billboard,
            Alignment alignment
    ) {
        return new EntityDisplayOptions(
                scale,
                rotationX,
                rotationY,
                background,
                backgroundColor,
                shadow,
                seeThrough,
                billboard,
                alignment
        );
    }

    public static EntityDisplayOptions defaults() {
        return of(
                new Vector3f(1f, 1f, 1f),
                0f,
                0f,
                false,
                Color.BLACK,
                true,
                false,
                Billboard.CENTER,
                Alignment.CENTER
        );
    }

    public static EntityDisplayOptions ofItemDisplay(
            Vector3f scale,
            float rotationX,
            float rotationY,
            Billboard billboard
    ) {
        return of(
                scale,
                rotationX,
                rotationY,
                false,
                Color.BLACK,
                false,
                false,
                billboard,
                Alignment.CENTER
        );
    }

    public static EntityDisplayOptions defaultsToItemDisplays() {
        return ofItemDisplay(
                new Vector3f(1f, 1f, 1f),
                0f,
                0f,
                Billboard.FIXED
        );
    }

    public static EntityDisplayOptions withScale(Vector3f scale) {
        return of(
                scale,
                0f,
                0f,
                false,
                Color.BLACK,
                true,
                false,
                Billboard.CENTER,
                Alignment.CENTER
        );
    }

    public static EntityDisplayOptions withScaleRotation(Vector3f scale, float rotationX, float rotationY) {
        return of(
                scale,
                rotationX,
                rotationY,
                false,
                Color.BLACK,
                true,
                false,
                Billboard.CENTER,
                Alignment.CENTER
        );
    }

    public static EntityDisplayOptions withBackground(Color backgroundColor) {
        return of(
                new Vector3f(1f, 1f, 1f),
                0f,
                0f,
                true,
                backgroundColor,
                true,
                false,
                Billboard.CENTER,
                Alignment.CENTER
        );
    }

    public enum Billboard {
        FIXED,
        VERTICAL,
        HORIZONTAL,
        CENTER
    }
    public enum Alignment {
        LEFT,
        RIGHT,
        CENTER
    }

}