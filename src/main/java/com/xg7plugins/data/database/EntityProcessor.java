package com.xg7plugins.data.database;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EntityProcessor {

    private static String getSQLType(Class<?> clazz) {
        if (clazz == String.class) return "TEXT";
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
        return "TEXT";
    }

    public static void createTableOf(Plugin plugin, Class<?> clazz) {

        DBManager manager = XG7Plugins.getInstance().getDatabaseManager();

        CompletableFuture.runAsync(() -> {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE IF NOT EXISTS " + clazz.getSimpleName() + "(");
            Class<?> oneToManyClass = null;
            Field[] declaredFields = clazz.getDeclaredFields();
            List<String> fkeys = new ArrayList<>();
            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];

                if (field.isAnnotationPresent(Entity.PKey.class)) {
                    builder.append(field.getName() + " " + getSQLType(field.getType()) + " PRIMARY KEY");
                    if (field.getAnnotation(Entity.PKey.class).autoincrement()) builder.append(" AUTO_INCREMENT");

                    if (i == declaredFields.length - 1) break;

                    builder.append(", ");
                    continue;
                }
                if (field.isAnnotationPresent(Entity.FKey.class)) {
                    Entity.FKey fKey = field.getAnnotation(Entity.FKey.class);

                    fkeys.add("FOREIGN KEY (" + field.getName() + ") REFERENCES " + fKey.table() + "(" + fKey.reference() + ")");

                }
                if (field.getType().equals(List.class)) {
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    Type tipoGenerico = parameterizedType.getActualTypeArguments()[0];

                    oneToManyClass = (Class<?>) tipoGenerico;

                    if (i == declaredFields.length - 1) {
                        builder.replace(builder.length() - 2, builder.length(), "");
                        break;
                    }

                    continue;
                }

                builder.append(field.getName() + " " + getSQLType(field.getType()) + " NOT NULL");

                if (i == declaredFields.length - 1) break;

                builder.append(", ");

            }

            fkeys.forEach(fkey -> builder.append(", ").append(fkey));
            builder.append(");");
            manager.executeUpdate(plugin,builder.toString());
            if (oneToManyClass != null) createTableOf(plugin, oneToManyClass);

        },XG7Plugins.getInstance().getTaskManager().getExecutor());
    }

    public static void insetEntity(Plugin plugin, Entity entity) {

        DBManager manager = XG7Plugins.getInstance().getDatabaseManager();

        CompletableFuture.runAsync(() -> {
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO " + entity.getClass().getSimpleName() + " VALUES (");

            Arrays.stream(entity.getClass().getDeclaredFields()).filter(field -> !field.getType().equals(List.class)).map(field -> "?,").forEach(builder::append);
            builder.replace(builder.length() - 1, builder.length(), ")");

            List<Object> args = new ArrayList<>();
            List<Object> childs = new ArrayList<>();

            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().equals(List.class)) {

                    try {
                        List<?> list = (List<?>) field.get(entity);
                        childs.addAll(list);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                    continue;
                }
                try {
                    args.add(field.get(entity));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            manager.executeUpdate(plugin, builder.toString(), args.toArray());
            if (!childs.isEmpty()) childs.forEach(item -> insetEntity(plugin, ((Entity) item)));

        },XG7Plugins.getInstance().getTaskManager().getExecutor());
    }

}
