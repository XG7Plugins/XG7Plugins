package com.xg7plugins.data.database.query;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Table;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.entity.Pkey;
import com.xg7plugins.data.database.processor.TableCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.*;
import java.util.*;

@AllArgsConstructor
@Getter
@ToString

public class QueryResult {

    private final Plugin plugin;
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
        if (resultsMap == null || !resultsMap.hasNext()) return null;

        return get(clazz, resultsMap.next(), true);
    }

    private <T extends Entity> T get(Class<T> clazz, Map<String, Object> result, boolean cache)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        if (result == null) return null;

        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T instance = (T) constructor.newInstance();

        Object id = null;

        String tableName = clazz.isAnnotationPresent(Table.class)
                ? clazz.getAnnotation(Table.class).name().toLowerCase()
                : clazz.getSimpleName().toLowerCase();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            if (Modifier.isTransient(field.getModifiers())) continue;

            String fieldName = field.isAnnotationPresent(Column.class)
                    ? field.getAnnotation(Column.class).name()
                    : field.getName();

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

            Object value = result.get(tableName + "." + fieldName);

            if (UUID.class.isAssignableFrom(field.getType()) && value != null) {
                field.set(instance, UUID.fromString(value.toString()));
                continue;
            }

            if ((Boolean.class.isAssignableFrom(field.getType()) || boolean.class.isAssignableFrom(field.getType())) && value != null) {
                field.set(instance, value instanceof Integer ?  (Integer) value == 1 : value);
                continue;
            }

            if ((Float.class.isAssignableFrom(field.getType()) || float.class.isAssignableFrom(field.getType())) && value != null) {
                field.set(instance, ((Number) value).floatValue());
                continue;
            }

            if (TableCreator.getSQLType(field.getType()) == null) {
                Constructor<?> constructorOfO = field.getType().getDeclaredConstructor();
                constructorOfO.setAccessible(true);

                Object nestedInstance = constructorOfO.newInstance();

                for (Field nestedField : nestedInstance.getClass().getDeclaredFields()) {
                    nestedField.setAccessible(true);
                    String nestedFieldName = nestedField.isAnnotationPresent(Column.class)
                            ? nestedField.getAnnotation(Column.class).name()
                            : nestedField.getName();

                    Object nestedValue = result.get(tableName + "." + nestedFieldName);

                    if (nestedValue != null) {
                        if (UUID.class.isAssignableFrom(nestedField.getType())) nestedField.set(nestedInstance, UUID.fromString(nestedValue.toString()));
                        else if ((Float.class == nestedField.getType() || float.class == nestedField.getType())) nestedField.set(nestedInstance, ((Number) nestedValue).floatValue());
                        else nestedField.set(nestedInstance, nestedValue);
                    }
                }
                field.set(instance, nestedInstance);
                continue;
            }

            field.set(instance, value);
        }

        if (cache && id != null) {
            XG7Plugins.getInstance().getDatabaseManager().cacheEntity(plugin, id.toString(), instance);
        }

        return instance;
    }




}
