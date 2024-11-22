package com.xg7plugins.utils;

import java.util.function.Function;

public enum Parser {
    INTEGER(Integer::parseInt),
    STRING(s -> s),
    BOOLEAN(Boolean::parseBoolean),
    LONG(Long::parseLong),
    DOUBLE(Double::parseDouble),
    FLOAT(Float::parseFloat),
    SHORT(Short::parseShort),
    BYTE(Byte::parseByte),
    CHAR(s -> s.charAt(0));

    private final Function<String, ?> converter;

    Parser(Function<String, ?> converter) {
        this.converter = converter;
    }

    public <T> T convert(String value) {
        return (T) converter.apply(value);
    }
}
