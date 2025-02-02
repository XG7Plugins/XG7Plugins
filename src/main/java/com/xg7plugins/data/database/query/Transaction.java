package com.xg7plugins.data.database.query;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.entity.*;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.processor.TableCreator;
import com.xg7plugins.utils.Pair;
import lombok.Getter;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Transaction {

    @Getter
    private final Plugin plugin;

    @Getter
    private Runnable success;

    private final CountDownLatch latch = new CountDownLatch(1);

    @Getter
    private Consumer<Exception> error;

    private Type type;

    private String table;
    private final List<String> columns = new ArrayList<>();
    @Getter
    private final List<Object> params = new ArrayList<>();

    @Getter
    private final List<Pair<String, List<Object>>> queries = new ArrayList<>();
    private String condition;

    public Transaction(Plugin plugin) {
        this.plugin = plugin;
    }

    public static Transaction create(Plugin plugin) {
        return new Transaction(plugin);
    }

    public static Transaction createTransaction(Plugin plugin, Entity entity, Type type)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        Transaction transaction = new Transaction(plugin);

        List<Pair<Type, Entity>> entitiesToUpdate = new ArrayList<>();
        List<Pair<String, List<Object>>> commandsToAdd = new ArrayList<>();

        entitiesToUpdate.add(new Pair<>(type, entity));

        int index = 0;
        while (index < entitiesToUpdate.size()) {
            Entity entityToUpdate = entitiesToUpdate.get(index).getSecond();
            Class<? extends Entity> entityClass = entityToUpdate.getClass();
            type = entitiesToUpdate.get(index).getFirst();

            String tableName = entityClass.isAnnotationPresent(Table.class)
                    ? entityClass.getAnnotation(Table.class).name()
                    : entityClass.getSimpleName();

            transaction.setType(type);
            transaction.onTable(tableName);

            String idName = null;
            Object idValue = null;

            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);

                if (Modifier.isTransient(field.getModifiers())) continue;

                Object value = field.get(entityToUpdate);

                if (value == null) continue;

                if (Collection.class.isAssignableFrom(field.getType())) {

                    java.lang.reflect.Type genericType = field.getGenericType();
                    if (!(genericType instanceof ParameterizedType)) continue;

                    List<? extends Entity> objectList = (List<? extends Entity>) value;

                    if (type != Type.UPDATE) {
                        for (Entity object : objectList) entitiesToUpdate.add(new Pair<>(type, object));
                        continue;
                    }

                    String idTable = null;
                    Class<? extends Entity> entityType = (Class<? extends Entity>)
                            ((ParameterizedType) genericType).getActualTypeArguments()[0];

                    for (Field fieldOfInsideOb : entityType.getDeclaredFields()) {
                        fieldOfInsideOb.setAccessible(true);

                        if (Modifier.isTransient(fieldOfInsideOb.getModifiers())) continue;

                        if (fieldOfInsideOb.isAnnotationPresent(FKey.class) &&
                                fieldOfInsideOb.getAnnotation(FKey.class).origin_table().equals(entityClass)) {
                            idTable = fieldOfInsideOb.isAnnotationPresent(Column.class)
                                    ? fieldOfInsideOb.getAnnotation(Column.class).name()
                                    : fieldOfInsideOb.getName();
                            break;
                        }
                    }

                    if (idValue == null) continue;

                    QueryResult result = Query.selectFrom(plugin, entityType.isAnnotationPresent(Table.class)
                                    ? entityType.getAnnotation(Table.class).name()
                                    : entityType.getSimpleName())
                            .allColumns()
                            .where(idTable + " = ?")
                            .params(idValue)
                            .waitForResult();

                    List<Entity> databaseList = new ArrayList<>();
                    while (result.hasNext()) {
                        databaseList.add(result.get(entityType));
                    }

                    for (Entity object : objectList) {

                        boolean exists = databaseList.stream().anyMatch(dbObject -> object.equals(dbObject));

                        if (exists) entitiesToUpdate.add(new Pair<>(Type.UPDATE, object));
                        else entitiesToUpdate.add(new Pair<>(Type.INSERT, object));

                        if (!databaseList.isEmpty()) databaseList.removeIf(dbObject -> dbObject.equals(object));
                    }

                    for (Entity object : databaseList) {
                        entitiesToUpdate.add(new Pair<>(Type.DELETE, object));
                    }

                    continue;
                }

                if (field.isAnnotationPresent(Pkey.class)) {
                    idName = field.isAnnotationPresent(Column.class)
                            ? field.getAnnotation(Column.class).name()
                            : field.getName();
                    idValue = value;

                    if (type == Type.UPDATE) continue;
                    if (type == Type.DELETE) break;
                }

                if (type == Type.DELETE) continue;

                if (TableCreator.getSQLType(field.getType()) == null) {
                    for (Field fieldOfInsideOb : field.getType().getDeclaredFields()) {
                        fieldOfInsideOb.setAccessible(true);

                        if (Modifier.isTransient(fieldOfInsideOb.getModifiers())) continue;

                        String columnName = fieldOfInsideOb.isAnnotationPresent(Column.class)
                                ? fieldOfInsideOb.getAnnotation(Column.class).name()
                                : fieldOfInsideOb.getName();

                        Object nestedValue = fieldOfInsideOb.get(value);
                        transaction.addColumns(columnName);
                        transaction.params(nestedValue);

                    }
                    continue;
                }

                String columnName = field.isAnnotationPresent(Column.class)
                        ? field.getAnnotation(Column.class).name()
                        : field.getName();

                transaction.addColumns(columnName);
                transaction.params(value);
            }

            if (type != Type.INSERT) {
                transaction.where(idName + " = ?");
                transaction.params(idValue);
            }

            transaction.newCommand();
            index++;
        }

        transaction.addCommands(commandsToAdd.toArray(new Pair[0]));

        return transaction;
    }



    public static Transaction update(Plugin plugin, Entity entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        return createTransaction(plugin, entity, Type.UPDATE);
    }
    public static Transaction insert(Plugin plugin, Entity entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        return createTransaction(plugin, entity, Type.INSERT);
    }
    public static Transaction delete(Plugin plugin, Entity entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        return createTransaction(plugin, entity, Type.DELETE);
    }

    public Transaction newCommand() {
        this.queries.add(new Pair<>(type.buildQuery(table, columns, condition), new ArrayList<>(params)));
        this.params.clear();
        this.columns.clear();
        this.table = null;
        this.condition = null;
        return this;
    }

    public Transaction addCommands(Pair<String, List<Object>>... commands) {
        this.queries.addAll(Arrays.asList(commands));
        return this;
    }

    public Transaction onTable(String tableName) {
        this.table = tableName;
        return this;
    }
    public Transaction setType(Type type) {
        this.type = type;
        return this;
    }
    public Transaction addColumns(String... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    public Transaction where(String condition) {
        this.condition = condition;
        return this;
    }

    public Transaction params(Object... params) {
        Collections.addAll(this.params, params);
        return this;
    }

    public Transaction onSuccess(Runnable success) {
        this.success = success;
        return this;
    }

    public Transaction onError(Consumer<Exception> error) {
        this.error = error;
        return this;
    }

    public Transaction update() {
        this.type = Type.UPDATE;
        return this;
    }
    public Transaction insert() {
        this.type = Type.INSERT;
        return this;
    }
    public Transaction delete() {
        this.type = Type.DELETE;
        return this;
    }

    public Transaction queue() {
        XG7Plugins.getInstance().getDatabaseManager().getProcessor().queueTransaction(this);
        return this;
    }

    public enum Type {
        INSERT,
        UPDATE,
        DELETE;

        public String buildQuery(String table , List<String> columns, String condition) {
            StringBuilder query = new StringBuilder();

            if (table == null) {
                throw new IllegalArgumentException("Table name cannot be null");
            }

            switch (this) {
                case INSERT:
                    query.append("INSERT INTO ").append(table).append(" (");

                    for (int i = 0; i < columns.size(); i++) {
                        query.append(columns.get(i));
                        if (i < columns.size() - 1) query.append(", ");
                    }

                    query.append(") VALUES (");
                    for (int i = 0; i < columns.size(); i++) {
                        query.append("?");
                        if (i < columns.size() - 1) query.append(", ");
                    }
                    query.append(")");

                    return query.toString();

                case UPDATE:

                    query.append("UPDATE ").append(table).append(" SET ");

                    for (int i = 0; i < columns.size(); i++) {
                        query.append(columns.get(i)).append(" = ?");
                        if (i < columns.size() - 1) query.append(", ");
                    }

                    if (condition != null) query.append(" WHERE ").append(condition);

                    return query.toString();
                case DELETE:

                    query.append("DELETE FROM ").append(table);

                    if (condition != null) query.append(" WHERE ").append(condition);

                    return query.toString();
                default:
                    return "null";
            }
        }
    }


    public void waitForResult() {
        try {
            queue();
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public void completeTask() {
        latch.countDown();
    }
}
