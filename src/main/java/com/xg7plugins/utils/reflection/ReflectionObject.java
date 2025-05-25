package com.xg7plugins.utils.reflection;

import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * A utility class that provides reflection-based access to object fields and methods.
 * This class simplifies the process of accessing and modifying private fields
 * and invoking methods through reflection.
 */
@Getter
public class ReflectionObject {

    private final Object object;
    private final Class<?> objectClass;

    /**
     * Creates a new ReflectionObject instance.
     *
     * @param object The target object to reflect upon (must not be null)
     * @throws IllegalArgumentException if the object parameter is null
     */
    public ReflectionObject(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        this.object = object;
        this.objectClass = object.getClass();
    }

    /**
     * Static factory method to create a ReflectionObject instance.
     *
     * @param object The target object to reflect upon
     * @return A new ReflectionObject instance
     */
    public static ReflectionObject of(Object object) {
        return new ReflectionObject(object);
    }

    /**
     * Sets the value of a field in the reflected object.
     *
     * @param name  The name of the field to set
     * @param value The value to set the field to
     */
    public void setField(String name, Object value) {
        try {
            Field field = objectClass.getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Static utility method to set a field value in any object.
     *
     * @param object The target object
     * @param name   The name of the field to set
     * @param value  The value to set the field to
     */
    public static void setField(Object object, String name, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the value of a field from the reflected object.
     *
     * @param name The name of the field to get
     * @param <T>  The type to cast the field value to
     * @return The value of the field, or null if an error occurs
     */
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

    /**
     * Gets the value of a field from a specific superclass of the reflected object.
     *
     * @param clazz The superclass containing the field
     * @param name  The name of the field to get
     * @param <T>   The type to cast the field value to
     * @return The value of the field, or null if an error occurs
     */
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

    /**
     * Gets a reflected method from the object's class.
     *
     * @param name           The name of the method to get
     * @param parameterTypes The parameter types of the method
     * @return A ReflectionMethod instance for the specified method
     */
    @SneakyThrows
    public ReflectionMethod getMethod(String name, Class<?>... parameterTypes) {
        return new ReflectionMethod(object, objectClass.getMethod(name, parameterTypes));
    }

}
