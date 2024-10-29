package com.xg7plugins.utils.reflection;

import com.xg7plugins.XG7Plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityDataWatcher1_17_1_XX {

    private static final ReflectionClass dataWatcherObjectClass;
    private static final ReflectionClass dataWatcherItemClass;
    private static final ReflectionClass dataWatcherItemArrayClass;
    private static final ReflectionClass dataWatcherRegistryClass;
    private static final ReflectionClass dataWatcherSerializerClass;
    private static final ReflectionClass dataWatcherClass;
    private static final ReflectionClass synchedDataHolderClass;
    private static final ReflectionClass iChatBaseComponentClass;
    private static final ReflectionClass entityClass;

    static {
        dataWatcherObjectClass = NMSUtil.getNewerNMSClass("network.syncher.DataWatcherObject");
        dataWatcherItemClass = NMSUtil.getNewerNMSClass("network.syncher.DataWatcher$Item");
        dataWatcherItemArrayClass = ReflectionClass.of("[Lnet.minecraft.network.syncher.DataWatcher$Item;");
        dataWatcherRegistryClass = NMSUtil.getNewerNMSClass("network.syncher.DataWatcherRegistry");
        dataWatcherSerializerClass = NMSUtil.getNewerNMSClass("network.syncher.DataWatcherSerializer");
        dataWatcherClass = NMSUtil.getNewerNMSClass("network.syncher.DataWatcher");
        iChatBaseComponentClass = NMSUtil.getNewerNMSClass("network.chat.IChatBaseComponent");
        entityClass = NMSUtil.getNewerNMSClass("world.entity.Entity");
        synchedDataHolderClass = XG7Plugins.getMinecraftVersion() >=21 ? NMSUtil.getNewerNMSClass("network.syncher.SyncedDataHolder") : null;
    }

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
            ReflectionObject object = dataWatcherObjectClass
                    .getConstructor(int.class, dataWatcherSerializerClass.getAClass())
                    .newInstance(index, dataWatcherRegistryClass.getStaticField(fieldType));

            ReflectionObject watcher = dataWatcherItemClass
                    .getConstructor(dataWatcherObjectClass.getAClass(), Object.class)
                    .newInstance(object.getObject(), value);


            watcher.getMethod("a", boolean.class).invoke(true);

            watchers.add(watcher.getObject());

    }

    public ReflectionObject getWatcher() {

        if (XG7Plugins.getMinecraftVersion() < 21) {

            ReflectionObject watcher = dataWatcherClass.getConstructor(entityClass.getAClass()).newInstance(entityClass.cast(null));

            watchers.forEach(w -> {

                ReflectionObject wi = ReflectionObject.of(w);

                if (XG7Plugins.getMinecraftVersion() < 19) watcher.getMethod("register", dataWatcherObjectClass.getAClass(), Object.class).invoke(wi.getMethod("a").invoke(), wi.getMethod("b").invoke());
                else watcher.getMethod("a", dataWatcherObjectClass.getAClass(), Object.class).invoke(wi.getMethod("a").invoke(), wi.getMethod("b").invoke());
            });

            return watcher;
        }

        ReflectionObject dataWatcher1_21 = dataWatcherClass
                .getConstructor(synchedDataHolderClass
                        .getAClass(), dataWatcherItemArrayClass.getAClass()
                ).newInstance(null, watchers.toArray());

        dataWatcher1_21.setField("f", true);

        return dataWatcher1_21;

    }

}
