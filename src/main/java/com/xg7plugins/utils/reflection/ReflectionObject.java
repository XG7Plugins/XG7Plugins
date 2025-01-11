package com.xg7plugins.utils.reflection;

import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

@Getter
public class ReflectionObject {

    private final Object object;
    private final Class<?> objectClass;

    public ReflectionObject(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        this.object = object;
        this.objectClass = object.getClass();
    }

    public static ReflectionObject of(Object object) {
        return new ReflectionObject(object);
    }

    public void setField(String name, Object value) {
        try {
            Field field = objectClass.getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setField(Object object, String name, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T getField(String name) {
        try {
            Field field = objectClass.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public <T> T getFieldFromSuperClass(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public ReflectionMethod getMethod(String name, Class<?>... parameterTypes) {
        return new ReflectionMethod(object, objectClass.getMethod(name, parameterTypes));
    }

}
