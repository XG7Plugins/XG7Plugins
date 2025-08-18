package com.xg7plugins.data.config.section;

import com.xg7plugins.utils.time.Time;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public enum DefaultValues {

    STRING(""),
    INT(0),
    DOUBLE(0.0),
    FLOAT(0.0f),
    LONG(0L),
    BOOLEAN(false),
    TIME(Time.of(0)),
    LIST(new ArrayList<>()),
    MAP(new java.util.HashMap<>()),
    DEFAULT(null);

    private final Object defaultValue;

    DefaultValues(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public static DefaultValues ofType(Class<?> type) {

        switch (type.getSimpleName()) {
            case "String":
                return STRING;
            case "Integer":
            case "int":
                return INT;
            case "Double":
            case "double":
                return DOUBLE;
            case "Float":
            case "float":
                return FLOAT;
            case "Long":
            case "long":
                return LONG;
            case "Boolean":
            case "boolean":
                return BOOLEAN;
            case "Time":
                return TIME;
            case "List":
                return LIST;
            case "Map":
                return MAP;
            default:
                return DEFAULT;

        }

    }

}
