package com.xg7plugins.utils;

public abstract class Builder<T, B extends Builder<T,B>> {

    public abstract T build(Object... args);

}
