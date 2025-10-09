package com.xg7plugins.utils.reflection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for working with Java reflection on classes.
 * Provides a wrapper around Class objects to simplify common reflection operations.
 * 
 * This class handles operations such as:
 * - Creating new instances
 * - Accessing constructors
 * - Getting static fields
 * - Getting enum values
 * - Accessing methods
 * - Type casting
 * - Working with annotations
 * 
 * It serves as a core component for the reflection utility package,
 * making reflection operations more streamlined and less error-prone.
 */
@RequiredArgsConstructor
public class ReflectionClass {

    @Getter
    private final Class<?> aClass;
    private Constructor<?> constructor;

    /**
     * Creates a new instance of the class using the currently set constructor.
     * 
     * @param args Arguments to pass to the constructor
     * @return A ReflectionObject wrapping the newly created instance
     */
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

    /**
     * Gets the array class representation of this class.
     * 
     * @return The Class object representing an array of this class type
     */
    public Class<?> getArrayClass() {
        try {
            return Class.forName("[L" + aClass.getName() + ";");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the constructor to use for creating new instances.
     * 
     * @param parameterTypes The parameter types of the constructor
     * @return This ReflectionClass instance for method chaining
     */
    public ReflectionClass getConstructor(Class<?>... parameterTypes) {
        try {
            constructor = aClass.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Gets a static field value from the class.
     * 
     * @param name The name of the static field
     * @param <T> The expected return type
     * @return The value of the static field
     */
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

    /**
     * Gets an enum constant from an enum class.
     * 
     * @param name The name of the enum constant
     * @param <T> The enum type
     * @return The enum constant
     */
    public <T extends Enum<T>> T getEnumField(String name) {
        return Enum.valueOf((Class<T>) aClass, name);
    }

    /**
     * Creates a ReflectionClass instance from a class name.
     * 
     * @param name The fully qualified class name
     * @return A new ReflectionClass instance
     */
    @Contract("_ -> new")
    public static @NotNull ReflectionClass of(String name) {
        try {
            return new ReflectionClass(Class.forName(name));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a ReflectionClass instance from a Class object.
     * 
     * @param clazz The Class object
     * @return A new ReflectionClass instance
     */
    @Contract("_ -> new")
    public static @NotNull ReflectionClass of(Class<?> clazz) {
        return new ReflectionClass(clazz);
    }

    /**
     * Gets a method from the class.
     * 
     * @param name The name of the method
     * @param parameterTypes The parameter types of the method
     * @return A ReflectionMethod object for the specified method
     */
    @SneakyThrows
    public ReflectionMethod getMethod(String name, Class<?>... parameterTypes) {
        return new ReflectionMethod(aClass.getMethod(name, parameterTypes));
    }

    public List<ReflectionMethod> getMethods() {
        List<ReflectionMethod> methods = new ArrayList<>();

        for (Method method : aClass.getDeclaredMethods()) {
            methods.add(new ReflectionMethod(method));
        }

        return methods;
    }

    /**
     * Casts an object to the type represented by this class.
     * 
     * @param o The object to cast
     * @return The cast object
     */
    public Object cast(Object o) {
        return aClass.cast(o);
    }

    /**
     * Casts an object to the type represented by this class and wraps it in a ReflectionObject.
     * 
     * @param o The object to cast
     * @return A ReflectionObject wrapping the cast object
     */
    public ReflectionObject castToRObject(Object o) {
        return new ReflectionObject(aClass.cast(o));
    }

    /**
     * Checks if the specified object is an instance of the class represented by this ReflectionClass.
     * 
     * @param object The object to check
     * @return true if the object is an instance of this class, false otherwise
     */
    public boolean isInstance(Object object) {
        return aClass.isInstance(object);
    }

    /**
     * Checks if the class has the specified annotation.
     * 
     * @param annotationClass The annotation class to check for
     * @return true if the class has the annotation, false otherwise
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return aClass.isAnnotationPresent(annotationClass);
    }

    /**
     * Gets the specified annotation from the class.
     * 
     * @param annotationClass The annotation class to get
     * @param <T> The annotation type
     * @return The annotation instance
     */
    @SneakyThrows
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return aClass.getAnnotation(annotationClass);
    }

    /**
     * Gets an inner class by name.
     * 
     * @param name The name of the inner class
     * @return The Class object for the inner class
     */
    @SneakyThrows
    public Class<?> getClassInside(String name) {
        return Class.forName(aClass.getName() + "$" + name);
    }

    public static boolean exists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}