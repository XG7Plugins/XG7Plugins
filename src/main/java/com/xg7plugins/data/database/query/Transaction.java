package com.xg7plugins.data.database.query;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.entity.Pkey;
import com.xg7plugins.data.database.entity.Table;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Pair;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

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

    public static Transaction createTransaction(Plugin plugin, Entity entity, Type type) throws IllegalAccessException {
        Transaction transaction = new Transaction(plugin);

        transaction.setType(type);

        List<Entity> entitiesToUpdate = new ArrayList<>();

        List<Pair<String, List<Object>>> commandsToAdd = new ArrayList<>();

        entitiesToUpdate.add(entity);

        int index = 0;
        while (index < entitiesToUpdate.size()) {
            Entity entityToUpdate = entitiesToUpdate.get(index);

            Class<? extends Entity> entityClass = entityToUpdate.getClass();

            String tableName = entityClass.isAnnotationPresent(Table.class) ? entityClass.getAnnotation(Table.class).name() : entityClass.getSimpleName();

            transaction.onTable(tableName);

            String idName = null;
            Object idValue = null;

            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);

                Object value = field.get(entityToUpdate);

                if (value == null) continue;

                if (Collection.class.isAssignableFrom(field.getType())) {
                    entitiesToUpdate.addAll((Collection<? extends Entity>) value);
                    continue;
                }

                if (Entity.class.isAssignableFrom(field.getType())) {
                    Transaction entityTransaction = createTransaction(plugin, (Entity) value, type);
                    commandsToAdd.addAll(entityTransaction.getQueries());
                }

                if (field.isAnnotationPresent(Pkey.class)) {

                    idName = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name() : field.getName();
                    idValue = value;

                    if (type == Type.UPDATE) continue;
                    if (type == Type.DELETE) break;
                }
                if (type == Type.DELETE) continue;
                transaction.addColumns(field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name() : field.getName());
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

    public static Transaction update(Plugin plugin, Entity entity) throws IllegalAccessException {
        return createTransaction(plugin, entity, Type.UPDATE);
    }
    public static Transaction insert(Plugin plugin, Entity entity) throws IllegalAccessException {
        return createTransaction(plugin, entity, Type.INSERT);
    }
    public static Transaction delete(Plugin plugin, Entity entity) throws IllegalAccessException {
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
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public void completeTask() {
        latch.countDown();
    }
}
