package com.xg7plugins.utils.reflection;

import com.xg7plugins.XG7Plugins;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Optional;

public class EntityDataWatcher {

    private final ReflectionObject dataWatcher;


    public EntityDataWatcher() {
        dataWatcher = NMSUtil.getNMSClass("DataWatcher").getConstructor(NMSUtil.getNMSClass("Entity").getAClass()).newInstance(NMSUtil.getNMSClass("Entity").cast(null));
    }

    private static String getFieldByType(Class<?> clazz) {
        if (clazz == String.class) {
            if (XG7Plugins.getMinecraftVersion() < 13) return "d";
            return "f";
        } else if (clazz == Integer.class || clazz == int.class) {
            return "b";
        } else if (clazz == Float.class || clazz == float.class) {
            return "c";
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            if (XG7Plugins.getMinecraftVersion() < 13) return "h";
            return "i";
        } else if (clazz == Byte.class || clazz == byte.class) {
            return "a";
        } else {
            return null;
        }
    }

    public void watch(int index, Object value) {
        if (XG7Plugins.getMinecraftVersion() < 9) {
            dataWatcher.getMethod("a", int.class, Object.class).invoke(index, value);
            return;
        }

        String fieldType = getFieldByType(value.getClass());

        if (Objects.equals(fieldType, "f")) {
            value = Optional.of(NMSUtil.getNMSClass("ChatComponentText").getConstructor(String.class).newInstance(value).getObject());
        }

        ReflectionObject dataWatcherObject = NMSUtil.getNMSClass("DataWatcherObject")
                .getConstructor(int.class, NMSUtil.getNMSClass("DataWatcherSerializer").getAClass())
                .newInstance(index, NMSUtil.getNMSClass("DataWatcherRegistry").getStaticField(fieldType));

        dataWatcher.getMethod("register", dataWatcherObject.getObjectClass(), Object.class).invoke(dataWatcherObject.getObject(), value);
    }


    public Object getWatcher() {
        return dataWatcher.getObject();
    }
    public ReflectionObject getWatcherAsARObject() {
        return dataWatcher;
    }


}
