package com.xg7plugins.data.database;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import lombok.AllArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

@AllArgsConstructor
public class Query {

    private final Iterator<Map<String,Object>> results;

    private final DBManager dbManager;

    public static CompletableFuture<Query> create(Plugin plugin, String sql, Object... params) {
        return XG7Plugins.getInstance().getDatabaseManager().executeQuery(plugin, sql,params);
    }
    public static <T extends Entity> CompletableFuture<T> getEntity(Plugin plugin, String sql, Object id, Class<T> clazz) {

        DBManager manager = XG7Plugins.getInstance().getDatabaseManager();

        ScheduledExecutorService executorService = XG7Plugins.getInstance().getTaskManager().getExecutor();

        if (manager.getEntitiesCached().asMap().containsKey(id)) return CompletableFuture.supplyAsync(() -> (T) manager.getEntitiesCached().asMap().get(id), executorService);

        return XG7Plugins.getInstance().getDatabaseManager().executeQuery(plugin, sql, id).thenApply(q -> !q.hasNextLine() ? null : q.get(clazz));
    }

    public static CompletableFuture<Void> update(Plugin plugin, String sql, Object... params) {
        return XG7Plugins.getInstance().getDatabaseManager().executeUpdate(plugin, sql,params);
    }

    public boolean hasNextLine() {
        return results.hasNext();
    }
    public Map<String, Object> nextLine() {
        return results.next();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) results.next().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        try {
            Map<String, Object> values = results.next();

            Constructor<T> tConstructor = clazz.getDeclaredConstructor();
            tConstructor.setAccessible(true);

            T instance = tConstructor.newInstance();

            Object id = null;

            for (Field f : clazz.getDeclaredFields()) {
                f.setAccessible(true);
                Object value = values.get(clazz.getSimpleName() + "." + f.getName());

                if (value == null) continue;

                Entity.PKey pKey = f.getAnnotation(Entity.PKey.class);
                if (pKey != null) {
                    if (dbManager.getEntitiesCached().asMap().containsKey(value)) return (T) dbManager.getEntitiesCached().asMap().get(value);
                    id = value;
                }

                if (f.getType() == List.class) {
                    ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
                    Type tipoGenerico = parameterizedType.getActualTypeArguments()[0];

                    List<Object> tList = new ArrayList<>();

                    Constructor<?> constructor = ((Class<?>) tipoGenerico).getDeclaredConstructor();
                    constructor.setAccessible(true);

                    Object listInstance = constructor.newInstance();

                    for (Field fListf : ((Class<?>) tipoGenerico).getDeclaredFields()) {
                        fListf.setAccessible(true);
                        if (values.get(clazz.getSimpleName() + "." + fListf.getName()) == null) continue;
                        if (fListf.getType() == UUID.class) {
                            fListf.set(instance, UUID.fromString((String) value));
                            continue;
                        }
                        fListf.set(listInstance, values.get(clazz.getSimpleName() + "." + fListf.getName()));
                    }
                    tList.add(listInstance);
                    tList.addAll(getResultList((Class<?>) tipoGenerico));

                    f.set(instance, tList);

                    continue;
                }
                if (f.getType() == UUID.class) {
                    f.set(instance, UUID.fromString((String) value));
                    continue;
                }
                f.set(instance, value);
            }

            dbManager.cacheEntity(id, (Entity) instance);

            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public <T> CompletableFuture<T> getAsync(Class<T> clazz) {
        return CompletableFuture.supplyAsync(() -> get(clazz), XG7Plugins.getInstance().getTaskManager().getExecutor());
    }

    public <T> List<T> getResultList(Class<T> clazz) {
        List<T> tList = new ArrayList<>();
        while (results.hasNext()) {
            tList.add(get(clazz));
        }
        return tList;
    }
}

