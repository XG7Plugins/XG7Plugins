package com.xg7plugins.data.database.query;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Table;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.entity.Pkey;
import com.xg7plugins.data.database.processor.IllegalEntityException;
import com.xg7plugins.data.database.processor.TableCreator;
import com.xg7plugins.utils.time.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.*;
import java.util.*;

/**
 * Represents a database query result that provides methods to iterate over
 * and convert database results into entity objects.
 * Contains functionality for handling single and collection-based entity mapping.
 */
@AllArgsConstructor
@Getter
@ToString

public class QueryResult {

    private final Plugin plugin;
    private Iterator<Map<String,Object>> resultsMap;

    /**
     * Returns the next result map from the query results.
     *
     * @return Map containing the next row of query results
     */
    public Map<String,Object> next() {
        return resultsMap.next();
    }

    /**
     * Checks if there are more results available in the query.
     *
     * @return true if more results exist, false otherwise
     */
    public boolean hasNext() {
        return resultsMap.hasNext();
    }

    /**
     * Creates a clone of the current results map iterator.
     * This allows for multiple iterations over the same results.
     *
     * @return A new iterator containing copies of all remaining results
     */
    public Iterator<Map<String,Object>> cloneMap() {
        List<Map<String, Object>> cloneResultsMap = new ArrayList<>();

        while (resultsMap.hasNext()) cloneResultsMap.add(resultsMap.next());

        this.resultsMap = cloneResultsMap.iterator();

        return cloneResultsMap.iterator();
    }

    /**
     * Converts the next result into an entity object of the specified class.
     *
     * @param clazz The entity class to convert the result into
     * @param <T>   The type of entity to create
     *
     * @return A new instance of the entity populated with query results, or null if no results exist
     *
     * @throws NoSuchMethodException     if the entity class doesn't have a default constructor
     * @throws InvocationTargetException if the constructor throws an exception
     * @throws InstantiationException    if the class cannot be instantiated
     * @throws IllegalAccessException    if the constructor cannot be accessed
     * @throws IllegalEntityException    if the constructor doesn't exist
     */
    public <T extends Entity> T get(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IllegalEntityException {
        if (resultsMap == null || !resultsMap.hasNext()) return null;

        return get(clazz, resultsMap.next(), true);
    }

    public <T extends Entity> List<T> getList(Class<T> clazz) throws Exception {
        List<T> list = new ArrayList<>();
        while (resultsMap.hasNext()) {
            Map<String, Object> result = resultsMap.next();
            T entity = get(clazz, result, true);
            if (entity != null) list.add(entity);
        }
        return list;
    }

    /**
     * Internal method to convert a result map into an entity object.
     * Handles nested objects, collections, and primitive type conversions.
     *
     * @param clazz  The entity class to convert the result into
     * @param result The map containing the database result
     * @param cache  Whether to cache the created entity
     * @param <T>    The type of entity to create
     *
     * @return A new instance of the entity populated with the result data
     *
     * @throws NoSuchMethodException     if the entity class doesn't have a default constructor
     * @throws InvocationTargetException if the constructor throws an exception
     * @throws InstantiationException    if the class cannot be instantiated
     * @throws IllegalAccessException    if the constructor cannot be accessed
     * @throws IllegalEntityException    if the constructor doesn't exist
     */
    private <T extends Entity> T get(Class<T> clazz, Map<String, Object> result, boolean cache)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IllegalEntityException {

        try {
            clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalEntityException(clazz);
        }

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

            if (TableCreator.getSQLType(field.getType(), 0) == null) {
                Constructor<?> constructorOfO = field.getType().getDeclaredConstructor();
                constructorOfO.setAccessible(true);

                Object nestedInstance = constructorOfO.newInstance();

                for (Field nestedField : nestedInstance.getClass().getDeclaredFields()) {
                    nestedField.setAccessible(true);
                    String nestedFieldName = nestedField.isAnnotationPresent(Column.class)
                            ? nestedField.getAnnotation(Column.class).name()
                            : nestedField.getName();




                    Object nestedValue = result.get(tableName + "." + nestedFieldName);
                    setField(nestedField, nestedInstance, nestedValue);
                }

                field.set(instance, nestedInstance);
                continue;
            }

            setField(field, instance, value);
        }

        if (cache && id != null) {
            XG7Plugins.getAPI().database().cacheEntity(plugin, id.toString(), instance);
        }

        return instance;
    }

    private void setField(Field field, Object instance, Object value) throws IllegalAccessException {
        if (value == null) {
            field.set(instance, null);
            return;
        }

        if (UUID.class.isAssignableFrom(field.getType())) {
            field.set(instance, UUID.fromString(value.toString()));
            return;
        }

        if ((Boolean.class.isAssignableFrom(field.getType()) || boolean.class.isAssignableFrom(field.getType()))) {
            field.set(instance, value instanceof Integer ?  (Integer) value == 1 : value);
            return;
        }

        if ((Float.class.isAssignableFrom(field.getType()) || float.class.isAssignableFrom(field.getType()))) {
            field.set(instance, ((Number) value).floatValue());
            return;
        }

        if (Time.class.isAssignableFrom(field.getType())) {
            field.set(instance, Time.of(((Number) value).longValue()));
            return;
        }

        if (field.getType().isEnum()) {
            @SuppressWarnings("unchecked")
            Object[] constants = field.getType().getEnumConstants();
            for (Object constant : constants) {
                if (constant.toString().equalsIgnoreCase(value.toString())) {
                    field.set(instance, constant);
                    return;
                }
            }
            field.set(instance, null);
            return;
        }

        field.set(instance, value);

    }



}
