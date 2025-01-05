package com.xg7plugins.data.database.query;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Table;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.entity.Pkey;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@AllArgsConstructor
@Getter
public class QueryResult {

    private Iterator<Map<String,Object>> resultsMap;

    public Map<String,Object> next() {
        return resultsMap.next();
    }

    public boolean hasNext() {
        return resultsMap.hasNext();
    }

    public Iterator<Map<String,Object>> cloneMap() {
        List<Map<String, Object>> cloneResultsMap = new ArrayList<>();

        while (resultsMap.hasNext()) cloneResultsMap.add(resultsMap.next());

        this.resultsMap = cloneResultsMap.iterator();

        return cloneResultsMap.iterator();
    }

    public <T extends Entity> T get(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return get(clazz, cloneMap().next(), true);
    }

    private <T extends Entity> T get(Class<T> clazz, Map<String, Object> result, boolean cache) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (result == null) return null;

        T instance = clazz.getDeclaredConstructor().newInstance();
        Object id = null;

        String tableName = clazz.isAnnotationPresent(Table.class) ? clazz.getAnnotation(Table.class).name().toLowerCase() : clazz.getSimpleName().toLowerCase();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            String fieldName = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name() : field.getName();

            if (field.isAnnotationPresent(Pkey.class)) id = result.get(tableName + "." + fieldName);

            if (Collection.class.isAssignableFrom(field.getType())) {
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                Type type = listType.getActualTypeArguments()[0];

                if (!Entity.class.isAssignableFrom((Class<?>) type)) continue;

                Collection<Entity> list = new ArrayList<>();
                Iterator<Map<String, Object>> clonedResultsMap = cloneMap();

                while (clonedResultsMap.hasNext()) {
                    Map<String, Object> clonedResult = clonedResultsMap.next();

                    list.add(get((Class<? extends Entity>) type, clonedResult, false));
                }

                field.set(instance, list);
                continue;
            }

            if (UUID.class.isAssignableFrom(field.getType()) && result.get(tableName + "." + fieldName) != null) {
                field.set(instance, UUID.fromString(result.get(tableName + "." + fieldName).toString()));
                continue;
            }
            field.set(instance, result.get(tableName + "." + fieldName));
        }

        if (cache && id != null) XG7Plugins.getInstance().getDatabaseManager().cacheEntity(id.toString(), instance);

        return instance;
    }


}
