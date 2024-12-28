package com.xg7plugins.utils.reflection;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@AllArgsConstructor
public class ReflectionMethod {

    private Object object;
    private Method method;

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
    public ReflectionObject invokeToRObject(Object... args) {
        method.setAccessible(true);
        return new ReflectionObject(this.method.invoke(object, args));
    }

    @SneakyThrows
    public static ReflectionMethod of(Object object, String name, Class<?>... parameterTypes) {
        return new ReflectionMethod(object, object.getClass().getDeclaredMethod(name, parameterTypes));
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return method.isAnnotationPresent(annotationClass);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

}
