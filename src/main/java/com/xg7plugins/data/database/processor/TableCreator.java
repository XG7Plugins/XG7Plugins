package com.xg7plugins.data.database.processor;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.database.entity.*;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.utils.time.Time;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class responsible for analyzing Entity classes and creating corresponding database tables.
 * Provides functionality for:
 * - Mapping Java types to SQL data types
 * - Creating tables with primary and foreign key constraints
 * - Handling nested entities and collections
 * - Supporting table name customization through annotations
 */
public class TableCreator {

    private static final Map<Class<?>, String> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(String.class, "VARCHAR");
        TYPE_MAP.put(char.class, "CHAR");
        TYPE_MAP.put(Character.class, "CHAR");
        TYPE_MAP.put(int.class, "INT");
        TYPE_MAP.put(Integer.class, "INT");
        TYPE_MAP.put(long.class, "BIGINT");
        TYPE_MAP.put(Long.class, "BIGINT");
        TYPE_MAP.put(float.class, "FLOAT");
        TYPE_MAP.put(Float.class, "FLOAT");
        TYPE_MAP.put(double.class, "DOUBLE");
        TYPE_MAP.put(Double.class, "DOUBLE");
        TYPE_MAP.put(boolean.class, "BOOLEAN");
        TYPE_MAP.put(Boolean.class, "BOOLEAN");
        TYPE_MAP.put(byte[].class, "BLOB");
        TYPE_MAP.put(Time.class, "BIGINT");
        TYPE_MAP.put(UUID.class, "UUID");
    }

    /**
     * Maps Java class types to their corresponding SQL data types.
     * Supports primitive types, their wrappers, and common Java classes.
     *
     * @param clazz The Java class to map to SQL type
     * @return The corresponding SQL data type as a string, or null if the type cannot be mapped
     */
    public static String getSQLType(Class<?> clazz, int size) {
        String type = TYPE_MAP.get(clazz);
        if (type == null) return null;

        switch (type) {
            case "VARCHAR":
                int finalSize = (size < 1) ? (clazz == UUID.class ? 36 : 255) : size;
                return "VARCHAR(" + finalSize + ")";
            case "INT":
                return "INT(" + (size < 1 ? 11 : size) + ")";
            case "BIGINT":
                return "BIGINT(" + (size < 1 ? 20 : size) + ")";
            case "FLOAT":
                return "FLOAT(" + (size < 1 ? 12 : size) + ")";
            case "DOUBLE":
                return "DOUBLE(" + (size < 1 ? 22 : size) + ")";
            case "UUID":
                return "VARCHAR(" + (size < 1 ? 36 : size) + ")";
            default:
                return type;
        }
    }


    /**
     * Asynchronously creates a database table for the specified Entity class.
     * Handles nested entities, collections, and creates the necessary foreign key relationships.
     *
     * @param plugin The plugin requesting the table creation
     * @param clazz  The Entity class to create a table for
     * @return A CompletableFuture that completes when the table is created
     * @throws IllegalEntityException if the entity class doesn't have a no-args constructor
     */
    public CompletableFuture<Void> createTableOf(Plugin plugin, Class<? extends Entity> clazz) {

        try {
            clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalEntityException(clazz);
        }

        DatabaseManager databaseManager = XG7PluginsAPI.database();

        return CompletableFuture.runAsync(() -> {
            try {
                StringBuilder query = new StringBuilder();
                String tableName = clazz.isAnnotationPresent(Table.class) ? clazz.getAnnotation(Table.class).name() : clazz.getSimpleName();
                query.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(");
                List<Class<? extends Entity>> childs = new ArrayList<>();
                List<String> fkeys = new ArrayList<>();
                List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
                int i = 0;
                while (i < fields.size()) {
                    Field field = fields.get(i);
                    field.setAccessible(true);

                    if (Modifier.isTransient(field.getModifiers())) {
                        i++;
                        continue;
                    }

                    String columnName = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name() : field.getName();
                    int size = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).length() : -1;

                    if (Collection.class.isAssignableFrom(field.getType())) {
                        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                        Class<?> genericType = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                        if (Entity.class.isAssignableFrom(genericType)) childs.add((Class<? extends Entity>) genericType);
                        i++;
                        continue;
                    }

                    if (getSQLType(field.getType(),size) == null) {
                        Collections.addAll(fields, field.getType().getDeclaredFields());
                        i++;
                        continue;
                    }

                    query.append(columnName).append(" ").append(getSQLType(field.getType(),size));

                    if (field.isAnnotationPresent(Pkey.class)) query.append(" PRIMARY KEY");

                    query.append(", ");
                    if (field.isAnnotationPresent(FKey.class)) {
                        FKey fKey = field.getAnnotation(FKey.class);
                        String referenceName = fKey.origin_table().isAnnotationPresent(Table.class) ? fKey.origin_table().getAnnotation(Table.class).name() : fKey.origin_table().getSimpleName();

                        fkeys.add("FOREIGN KEY (" + columnName + ") REFERENCES " + referenceName + "(" + fKey.origin_column() + ") ON DELETE CASCADE");
                    }

                    i++;

                }

                fkeys.forEach(fkey -> query.append(fkey).append(", "));

                if (query.toString().endsWith(", ")) query.setLength(query.length() - 2);

                query.append(")");

                Connection connection = databaseManager.getConnection(plugin);

                connection.prepareStatement(query.toString()).executeUpdate();
                connection.commit();


                childs.forEach(child -> createTableOf(plugin, child).join());

            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, XG7PluginsAPI.taskManager().getExecutor("database"));
    }

}
