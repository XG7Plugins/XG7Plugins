package com.xg7plugins.data.database.processor;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.entity.*;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.entity.Column;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TableCreator {

    public static String getSQLType(Class<?> clazz) {
        if (clazz == String.class) return "VARCHAR(255)";
        else if (clazz == int.class || clazz == Integer.class) return "INT(11)";
        else if (clazz == long.class || clazz == Long.class) return "BIGINT";
        else if (clazz == float.class || clazz == Float.class) return "FLOAT";
        else if (clazz == double.class || clazz == Double.class) return "DOUBLE";
        else if (clazz == boolean.class || clazz == Boolean.class) return "BOOLEAN";
        else if (clazz == char.class || clazz == Character.class) return "CHAR";
        else if (clazz == byte[].class) return "BLOB";
        else if (clazz == Timestamp.class) return "TIMESTAMP";
        else if (clazz == Date.class) return "DATE";
        else if (clazz == Time.class) return "TIME";
        else if (clazz == UUID.class) return "VARCHAR(36)";
        return null;
    }

    public CompletableFuture<Void> createTableOf(Plugin plugin, Class<? extends Entity> clazz) {

        DatabaseManager databaseManager = XG7Plugins.getInstance().getDatabaseManager();

        return CompletableFuture.runAsync(() -> {
            try {
                StringBuilder query = new StringBuilder();
                String tableName = clazz.isAnnotationPresent(Table.class) ? clazz.getAnnotation(Table.class).name() : clazz.getSimpleName();
                query.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(");
                List<Class<? extends Entity>> childs = new ArrayList<>();
                List<String> fkeys = new ArrayList<>();
                List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
                int i = 0; // Índice inicial
                while (i < fields.size()) {
                    Field field = fields.get(i);
                    field.setAccessible(true);

                    if (Modifier.isTransient(field.getModifiers())) {
                        i++;
                        continue;
                    }

                    String columnName = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name() : field.getName();
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                        Class<?> genericType = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                        if (Entity.class.isAssignableFrom(genericType)) {
                            childs.add((Class<? extends Entity>) genericType);
                        }
                        i++;
                        continue;
                    }

                    if (getSQLType(field.getType()) == null) {

                        for (Field objectField : field.getType().getDeclaredFields()) {
                            fields.add(objectField);
                        }

                        i++;
                        continue;
                    }

                    query.append(columnName).append(" ").append(getSQLType(field.getType()));

                    if (field.isAnnotationPresent(Pkey.class)) query.append(" PRIMARY KEY");

                    query.append(", ");
                    if (field.isAnnotationPresent(FKey.class)) {
                        FKey fKey = field.getAnnotation(FKey.class);
                        String referenceName = fKey.origin_table().isAnnotationPresent(Table.class) ? fKey.origin_table().getAnnotation(Table.class).name() : fKey.origin_table().getSimpleName();

                        fkeys.add("FOREIGN KEY (" + columnName + ") REFERENCES " + referenceName + "(" + fKey.origin_column() + ") ON DELETE CASCADE");
                    }

                    i++;

                }

                for (String fkey : fkeys) {
                    query.append(fkey).append(", ");
                }

                if (query.toString().endsWith(", ")) {
                    query.setLength(query.length() - 2);
                }

                query.append(")");

                Connection connection = databaseManager.getConnection(plugin);

                connection.prepareStatement(query.toString()).executeUpdate();
                connection.commit();


                for (Class<? extends Entity> child : childs) createTableOf(plugin, child).join();


            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, databaseManager.getProcessor().getExecutorService());
    }

}
