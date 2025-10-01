package com.xg7plugins.utils.reflection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for handling method reflection operations.
 * Provides a simplified interface for invoking methods and handling method annotations.
 */
@AllArgsConstructor
public class ReflectionMethod {

    private Object object;
    @Getter
    private Method method;

    public ReflectionMethod(Method method) {
        this.method = method;
    }

    /**
     * Invokes the reflected method with the given arguments.
     *
     * @param args Arguments to pass to the method
     * @param <T>  Return type of the method
     * @return The result of the method invocation cast to type T
     */
    public <T> T invoke(Object... args) {
        try {
            method.setAccessible(true);
            return (T) this.method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    @SneakyThrows
    /**
     * Invokes the method and wraps the result in a ReflectionObject.
     * @param args Arguments to pass to the method
     * @return ReflectionObject wrapping the method's return value
     */
    public ReflectionObject invokeToRObject(Object... args) {
        method.setAccessible(true);
        return new ReflectionObject(this.method.invoke(object, args));
    }

    @SneakyThrows
    /**
     * Creates a new ReflectionMethod instance for the specified method.
     * @param object The object instance containing the method
     * @param name Name of the method to reflect
     * @param parameterTypes Parameter types of the method
     * @return New ReflectionMethod instance
     */
    public static ReflectionMethod of(Object object, String name, Class<?>... parameterTypes) {
        return new ReflectionMethod(object, object.getClass().getDeclaredMethod(name, parameterTypes));
    }

    /**
     * Checks if the method has the specified annotation.
     *
     * @param annotationClass The annotation class to check for
     * @return true if the method has the annotation, false otherwise
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return method.isAnnotationPresent(annotationClass);
    }

    /**
     * Gets the specified annotation from the method if present.
     *
     * @param annotationClass The annotation class to retrieve
     * @param <T>             The type of the annotation
     * @return The annotation instance if present, null otherwise
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    public List<Class<?>> getParamsTypes() {
        return Arrays.asList(method.getParameterTypes());
    }

}