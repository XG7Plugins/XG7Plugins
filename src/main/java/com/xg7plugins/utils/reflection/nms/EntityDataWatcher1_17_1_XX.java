package com.xg7plugins.utils.reflection.nms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityDataWatcher1_17_1_XX {

    private static final ReflectionClass iChatBaseComponentClass = NMSUtil.getNewerNMSClass("network.chat.IChatBaseComponent");

    private static final ReflectionClass dataWatcherObjectClass = NMSUtil.getNewerNMSClass("network.syncher.DataWatcherObject");
    private static final ReflectionClass dataWatcherRegistryClass = NMSUtil.getNewerNMSClass("network.syncher.DataWatcherRegistry");
    private static final ReflectionClass dataWatcherSerializerClass = NMSUtil.getNewerNMSClass("network.syncher.DataWatcherSerializer");
    private static final ReflectionClass dataWatcherItemClass = NMSUtil.getNewerNMSClass("network.syncher.DataWatcher$Item");
    private static final ReflectionClass dataWatcherClass = NMSUtil.getNewerNMSClass("network.syncher.DataWatcher");
    private static final ReflectionClass entityClass = NMSUtil.getNewerNMSClass("world.entity.Entity");


    private final List<Object> watchers;

    public EntityDataWatcher1_17_1_XX() {
        watchers = new ArrayList<>();
    }
    private static String getFieldByType(Class<?> clazz) {
        if (clazz == String.class) {
            return XG7Plugins.getMinecraftVersion() < 19 ? "f" : "g";
        } else if (clazz == Integer.class || clazz == int.class) {
            return "b";
        } else if (clazz == Float.class || clazz == float.class) {
            return XG7Plugins.getMinecraftVersion() < 19 ? "i" : "d";
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return XG7Plugins.getMinecraftVersion() < 19 ? "i" : "k";
        } else if (clazz == Byte.class || clazz == byte.class) {
            return "a";
        } else {
            return null;
        }
    }
    public <T> void watch(int index, T value) {
        String fieldType = getFieldByType(value.getClass());
        if (fieldType.equals("f") || fieldType.equals("g")) {
            value = (T) Optional.of(iChatBaseComponentClass.getMethod("a", String.class).invoke(value.toString()));
        }

        ReflectionObject dataObject = dataWatcherObjectClass
                .getConstructor(int.class, dataWatcherSerializerClass.getAClass())
                .newInstance(index, dataWatcherRegistryClass.getStaticField(fieldType));

        ReflectionObject item = dataWatcherItemClass
                .getConstructor(dataWatcherObjectClass.getAClass(), Object.class)
                .newInstance(dataObject.getObject(), value);

        item.getMethod("a", boolean.class).invoke(true);

        watchers.add(item.getObject());
    }
    public ReflectionObject getWatcher() {
        if (XG7Plugins.getMinecraftVersion() < 21) {

            ReflectionObject dataWatcher = dataWatcherClass
                    .getConstructor(entityClass.getAClass())
                    .newInstance(entityClass.cast(null));

            watchers.forEach(w -> {

                ReflectionObject wi = ReflectionObject.of(w);
                Object dataWatcherObject = wi.getMethod("a").invoke();

                dataWatcher.getMethod(XG7Plugins.getMinecraftVersion() < 19 ? "register" : "a", dataWatcherObjectClass.getAClass(), Object.class).invoke(dataWatcherObject, wi.getMethod("b").invoke());

                dataWatcher.getMethod("markDirty", dataWatcherObjectClass.getAClass()).invoke(dataWatcherObject);

            });

            return dataWatcher;
        }

        Object watchersArray = Array.newInstance(dataWatcherItemClass.getAClass(), watchers.size());
        for (int i = 0; i < watchers.size(); i++) {
            Array.set(watchersArray, i, watchers.get(i));
        }

        ReflectionObject dataWatcher1_21 = dataWatcherClass
                .getConstructor(ReflectionClass.of("net.minecraft.network.syncher.SyncedDataHolder")
                        .getAClass(), dataWatcherItemClass.getArrayClass()
                ).newInstance(null, watchersArray);

        dataWatcher1_21.setField("f", true);

        return dataWatcher1_21;
    }
}
