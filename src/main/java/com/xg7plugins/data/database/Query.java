package com.xg7plugins.data.database;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

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
    public static <T extends Entity> CompletableFuture<T> getEntity(Plugin plugin, String sql, String id, Class<T> clazz) {

        DBManager manager = XG7Plugins.getInstance().getDatabaseManager();

        if (manager.getEntitiesCached().asMap().containsKey(id)) return CompletableFuture.completedFuture((T) manager.getEntitiesCached().asMap().get(id));

        return XG7Plugins.getInstance().getDatabaseManager().executeQuery(plugin, sql, id).thenCompose(query -> {

            if (!query.hasNextLine()) return CompletableFuture.completedFuture(null);

            return CompletableFuture.supplyAsync(() -> query.get(clazz), XG7Plugins.taskManager().getAsyncExecutors().get("database"));
        });

    }

    @SneakyThrows
    public static CompletableFuture<Void> update(Plugin plugin, Entity entity) {

        DBManager manager = XG7Plugins.getInstance().getDatabaseManager();

        StringBuilder sql = new StringBuilder("UPDATE " + entity.getClass().getSimpleName() + " SET ");

        Pair<String, Object> id = new Pair<>(null, null);

        List<Object> params = new ArrayList<>();
        int index = 0;

        for (Field field : entity.getClass().getDeclaredFields()) {
            index++;
            field.setAccessible(true);
            if (field.isAnnotationPresent(Entity.PKey.class)){
                id = new Pair<>(field.getName(), field.get(entity));
                continue;
            }
            sql.append(field.getName()).append(index == entity.getClass().getDeclaredFields().length ? "= ?" : " = ?,");
            params.add(field.get(entity));
        }

        sql.append(" WHERE ").append(id.getFirst()).append(" = ?");

        params.add(id.getSecond());


        final Pair<String, Object> finalId = id;

        return manager.executeUpdate(plugin, sql.toString(), params.toArray()).thenRun(() -> manager.cacheEntity(finalId.getSecond(), entity));


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
    public <T extends Entity> T get(Class<T> clazz) {
        if (!hasNextLine()) return null;
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

                if (f.isAnnotationPresent(Entity.PKey.class)) {
                    if (dbManager.getEntitiesCached().asMap().containsKey(value.toString())) return (T) dbManager.getEntitiesCached().asMap().get(value);
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
                    tList.addAll(getResultList((Class<? extends Entity>) tipoGenerico));

                    f.set(instance, tList);

                    continue;
                }
                if (f.getType() == UUID.class) {
                    f.set(instance, UUID.fromString((String) value));
                    continue;
                }
                f.set(instance, value);
            }

            dbManager.cacheEntity(id, instance);

            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public <T extends Entity> List<T> getResultList(Class<T> clazz) {
        List<T> tList = new ArrayList<>();
        while (results.hasNext()) {
            tList.add(get(clazz));
        }
        return tList;
    }
}

