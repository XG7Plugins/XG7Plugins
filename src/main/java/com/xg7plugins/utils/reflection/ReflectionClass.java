package com.xg7plugins.utils.reflection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@RequiredArgsConstructor
public class ReflectionClass {

    @Getter
    private final Class<?> aClass;
    private Constructor<?> constructor;

    public ReflectionObject newInstance(Object... args) {

        if (constructor == null) {
            try {
                return new ReflectionObject(aClass.getConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return new ReflectionObject(constructor.newInstance(args));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ReflectionClass getConstructor(Class<?>... parameterTypes) {
        try {
            constructor = aClass.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public <T> T getStaticField(String name) {
        try {
            Field field = aClass.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Contract("_ -> new")
    public static @NotNull ReflectionClass of(String name) {
        try {
            return new ReflectionClass(Class.forName(name));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    @Contract("_ -> new")
    public static @NotNull ReflectionClass of(Class<?> clazz) {
        return new ReflectionClass(clazz);
    }

    @SneakyThrows
    public ReflectionMethod getMethod(String name, Class<?>... parameterTypes) {
        return new ReflectionMethod(null, aClass.getMethod(name, parameterTypes));
    }

    public Object cast(Object o) {
        return aClass.cast(o);
    }

    public ReflectionObject castToRObject(Object o) {
        return new ReflectionObject(aClass.cast(o));
    }
    @SneakyThrows
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return aClass.getAnnotation(annotationClass);
    }

}
