package com.xg7plugins.utils.reflection.nms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;

import java.lang.reflect.Field;

public class NMSUtil {

    @Getter
    private static String version;

    static {
        UnsafeValues unsafe = Bukkit.getUnsafe();
        version = unsafe.getClass().getName().split("\\.")[3];
    }

    public static ReflectionClass getNMSClass(String className) {
        String fullName = "net.minecraft.server." + version + "." + className;
        return ReflectionClass.of(fullName);
    }
    public static ReflectionClass getNewerNMSClass(String className) {
        String fullName = "net.minecraft." + className;
        return ReflectionClass.of(fullName);
    }

    public static ReflectionClass getCraftBukkitClass(String className) {
        String fullName = "org.bukkit.craftbukkit." + version + "." + className;
        return ReflectionClass.of(fullName);
    }

    public static ReflectionClass getNMSClassViaVersion(int targetVersion, String classNameOlder, String classNameNewer) {
        String fullName = XG7Plugins.getMinecraftVersion() >= targetVersion ? "net.minecraft." + classNameNewer : "net.minecraft.server." + version + "." + classNameOlder;
        return ReflectionClass.of(fullName);
    }

    @SneakyThrows
    public static <T> T getValueByFieldName(Object object,  String className) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.getType().getName().endsWith(className)) {
                field.setAccessible(true);
                return (T) field.get(object);
            }
        }
        return null;
    }
    @SneakyThrows
    public static Object getValueByFieldName(Class<?> clazz,  String className) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().getName().endsWith(className)) {
                field.setAccessible(true);
                return field.get(null);
            }
        }
        return null;
    }
    @SneakyThrows
    public static <T> T  getValueByFieldName(Class<?> superClass, Object object,  String className) {
        for (Field field : superClass.getDeclaredFields()) {
            if (field.getType().getName().endsWith(className)) {
                field.setAccessible(true);
                return (T) field.get(object);
            }
        }
        return null;
    }
    @SneakyThrows
    public static ReflectionObject getValueByFieldNameRObject(Object object, String className) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.getType().getName().endsWith(className)) {
                field.setAccessible(true);
                return ReflectionObject.of(field.get(object));
            }
        }
        return null;
    }
    @SneakyThrows
    public static ReflectionObject getValueByFieldNameRObject(Class<?> superClass, Object object,  String className) {
        for (Field field : superClass.getDeclaredFields()) {
            if (field.getType().getName().endsWith(className)) {
                field.setAccessible(true);
                return ReflectionObject.of(field.get(object));
            }
        }
        return null;
    }

}
