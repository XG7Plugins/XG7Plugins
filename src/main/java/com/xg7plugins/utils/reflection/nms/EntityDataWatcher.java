package com.xg7plugins.utils.reflection.nms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;

import java.lang.reflect.Array;
import java.util.*;

public class EntityDataWatcher {

    private final ReflectionObject dataWatcher;

    private static final ReflectionClass dataWatcherObjectClass = XG7Plugins.getMinecraftVersion() < 9 ? null : NMSUtil.getNMSClassViaVersion(17 , "DataWatcherObject", "network.syncher.DataWatcherObject");
    private static final ReflectionClass dataWatcherRegistryClass = XG7Plugins.getMinecraftVersion() < 9 ? null : NMSUtil.getNMSClassViaVersion(17 , "DataWatcherRegistry", "network.syncher.DataWatcherRegistry");
    private static final ReflectionClass dataWatcherSerializerClass = XG7Plugins.getMinecraftVersion() < 9 ? null : NMSUtil.getNMSClassViaVersion(17 , "DataWatcherSerializer", "network.syncher.DataWatcherSerializer");
    private static final ReflectionClass dataWatcherItemClass = XG7Plugins.getMinecraftVersion() < 9 ? null : NMSUtil.getNMSClassViaVersion(17 , "DataWatcher$Item", "network.syncher.DataWatcher$Item");
    private static final ReflectionClass dataWatcherClass = NMSUtil.getNMSClassViaVersion(17 , "DataWatcher", "network.syncher.DataWatcher");
    private static final ReflectionClass entityClass = XG7Plugins.getMinecraftVersion() > 20 ? NMSUtil.getNewerNMSClass("network.syncher.SyncedDataHolder") : NMSUtil.getNMSClassViaVersion(17 , "Entity", "world.entity.Entity");

    private final int version = XG7Plugins.getMinecraftVersion();


    public EntityDataWatcher() {
        dataWatcher = version < 17 ? dataWatcherClass.getConstructor(entityClass.getAClass()).newInstance(entityClass.cast(null)) : ReflectionObject.of(new ArrayList<>());
    }

    private String getFieldByType(Class<?> clazz) {

        if (clazz == String.class) {
            if (version < 13) return "d";
            if (version >= 19) return "g";
            return "f";
        } else if (clazz == Integer.class || clazz == int.class) {
            return "b";
        } else if (clazz == Float.class || clazz == float.class) {
            if (version > 16 && version < 19) return "i";
            if (version >= 19) return "d";
            return "c";
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            if (version < 13) return "h";
            if (version > 16 && version < 19) return "i";
            if (version >= 19) return "k";
            return "i";
        } else if (clazz == Byte.class || clazz == byte.class) {
            return "a";
        } else {
            return null;
        }
    }

    public void watch(int index, Object value) {
        if (version < 9) {
            dataWatcher.getMethod("a", int.class, Object.class).invoke(index, value);
            return;
        }

        String fieldType = getFieldByType(value.getClass());

        if (Objects.equals(fieldType, "f")) value = ChatComponent.of(value.toString()).getChatComponent();


        ReflectionObject dataWatcherObject = dataWatcherObjectClass
                .getConstructor(int.class, dataWatcherSerializerClass.getAClass())
                .newInstance(index, dataWatcherRegistryClass.getStaticField(fieldType));

        if (version > 16) {
            ReflectionObject item = dataWatcherItemClass
                    .getConstructor(dataWatcherObjectClass.getAClass(), Object.class)
                    .newInstance(dataWatcherObject.getObject(), value);

            item.getMethod("a", boolean.class).invoke(true);

            dataWatcher.getMethod("add", Object.class).invoke(item.getObject());
            return;
        }

        dataWatcher.getMethod("register", dataWatcherObject.getObjectClass(), Object.class).invoke(dataWatcherObject.getObject(), value);
    }


    public ReflectionObject getWatcher() {
        if (version < 17) return dataWatcher;
        List<Object> watchers = (List<Object>) dataWatcher.getObject();
        if (version < 21) {
            ReflectionObject dataWatcher = dataWatcherClass
                    .getConstructor(entityClass.getAClass())
                    .newInstance(entityClass.cast(null));

            watchers.forEach(w -> {

                ReflectionObject wi = ReflectionObject.of(w);
                Object dataWatcherObject = wi.getMethod("a").invoke();

                dataWatcher.getMethod(version < 19 ? "register" : "a", dataWatcherObjectClass.getAClass(), Object.class).invoke(dataWatcherObject, wi.getMethod("b").invoke());

                dataWatcher.getMethod("markDirty", dataWatcherObjectClass.getAClass()).invoke(dataWatcherObject);

            });

            return dataWatcher;
        }

        Object watchersArray = Array.newInstance(dataWatcherItemClass.getAClass(), watchers.size());
        for (int i = 0; i < watchers.size(); i++) {
            Array.set(watchersArray, i, watchers.get(i));
        }

        ReflectionObject dataWatcher1_21 = dataWatcherClass
                .getConstructor(entityClass.getAClass(), dataWatcherItemClass.getArrayClass()
                ).newInstance(null, watchersArray);

        dataWatcher1_21.setField("f", true);

        return dataWatcher1_21;

    }


}
