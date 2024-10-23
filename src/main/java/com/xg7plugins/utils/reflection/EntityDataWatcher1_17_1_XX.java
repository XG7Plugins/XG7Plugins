package com.xg7plugins.utils.reflection;

import com.xg7plugins.XG7Plugins;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityDataWatcher1_17_1_XX {

    private final List<DataWatcher.Item<Object>> watchers;

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
               value = (T) Optional.of(IChatBaseComponent.a(value.toString()));
            }
            DataWatcherObject<T> object = new DataWatcherObject<>(index, ReflectionClass.of(DataWatcherRegistry.class).getStaticField(fieldType));

            DataWatcher.Item<Object> watcher = (DataWatcher.Item<Object>) new DataWatcher.Item<>(object, value);

            watcher.a(true);

            watchers.add(watcher);

    }

    public DataWatcher getWatcher() {

        if (XG7Plugins.getMinecraftVersion() < 21) {
            DataWatcher watcher = new DataWatcher(null);

            watchers.forEach(w -> {
                if (XG7Plugins.getMinecraftVersion() < 19) watcher.register(w.a(), w.b());
                else ReflectionObject.of(watcher).getMethod("a", DataWatcherObject.class, Object.class).invoke(w.a(), w.b());
                watcher.markDirty(w.a());
            });

            return watcher;
        }

        ReflectionObject dataWatcher1_21 = ReflectionClass.of(DataWatcher.class)
                .getConstructor(ReflectionClass.of("net.minecraft.network.syncher.SyncedDataHolder")
                        .getAClass(), DataWatcher.Item[].class
                ).newInstance(null, watchers.toArray(new DataWatcher.Item[0]));

        dataWatcher1_21.setField("f", true);

        return (DataWatcher) dataWatcher1_21.getObject();

    }
    public ReflectionObject getWatcherRObject() {
        return ReflectionObject.of(getWatcher());
    }

}
