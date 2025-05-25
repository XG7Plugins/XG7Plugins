package com.xg7plugins.data.database.query;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
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

/**
 * Represents a database transaction that can handle multiple database operations (INSERT, UPDATE, DELETE)
 * in a single atomic unit. This class provides methods to build and execute database queries,
 * manage transaction states, and handle success/error callbacks.
 */
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

    /**
     * Creates a new Transaction instance.
     * @param plugin The plugin instance associated with this transaction
     */
    public Transaction(Plugin plugin) {
        this.plugin = plugin;
    }

    public static Transaction create(Plugin plugin) {
        return new Transaction(plugin);
    }

    /**
     * Creates a transaction for a specified entity with the given operation type.
     * Handles nested entities and their relationships automatically.
     * 
     * @param plugin The plugin instance
     * @param entity The entity to create the transaction for
     * @param type The type of operation (INSERT, UPDATE, DELETE)
     *
     * @return A configured Transaction instance
     *
     * @throws IllegalAccessException if unable to access entity fields
     * @throws InvocationTargetException if method invocation fails
     * @throws NoSuchMethodException if required method not found
     * @throws InstantiationException if unable to create instance
     */
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



    /**
     * Creates an UPDATE transaction for the specified entity.
     * @param plugin The plugin instance
     * @param entity The entity to update
     * @return Configured Transaction instance for UPDATE operation
     */
    public static Transaction update(Plugin plugin, Entity entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        return createTransaction(plugin, entity, Type.UPDATE);
    }
    
    /**
     * Creates an INSERT transaction for the specified entity.
     * @param plugin The plugin instance
     * @param entity The entity to insert
     * @return Configured Transaction instance for INSERT operation
     */
    public static Transaction insert(Plugin plugin, Entity entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        return createTransaction(plugin, entity, Type.INSERT);
    }
    
    /**
     * Creates a DELETE transaction for the specified entity.
     * @param plugin The plugin instance
     * @param entity The entity to delete
     * @return Configured Transaction instance for DELETE operation
     */
    public static Transaction delete(Plugin plugin, Entity entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        return createTransaction(plugin, entity, Type.DELETE);
    }

    /**
     * Adds a new command to the transaction and resets internal state
     */
    public Transaction newCommand() {
        this.queries.add(new Pair<>(type.buildQuery(table, columns, condition), new ArrayList<>(params)));
        this.params.clear();
        this.columns.clear();
        this.table = null;
        this.condition = null;
        return this;
    }

    /**
     * Adds multiple commands to the transaction
     *
     * @param commands Commands to add as Pair<String, List<Object>>
     */
    public Transaction addCommands(Pair<String, List<Object>>... commands) {
        this.queries.addAll(Arrays.asList(commands));
        return this;
    }

    /**
     * Sets the table name for this transaction
     *
     * @param tableName Name of the database table
     */
    public Transaction onTable(String tableName) {
        this.table = tableName;
        return this;
    }

    /**
     * Sets the operation type for this transaction
     *
     * @param type The Type enum value (INSERT, UPDATE, DELETE)
     */
    public Transaction setType(Type type) {
        this.type = type;
        return this;
    }

    /**
     * Adds columns to include in the transaction
     *
     * @param columns Column names to add
     */
    public Transaction addColumns(String... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    /**
     * Sets the WHERE condition for this transaction
     *
     * @param condition SQL WHERE clause condition
     */
    public Transaction where(String condition) {
        this.condition = condition;
        return this;
    }

    /**
     * Adds parameters for the prepared statement
     *
     * @param params Query parameters
     */
    public Transaction params(Object... params) {
        Collections.addAll(this.params, params);
        return this;
    }

    /**
     * Sets success callback
     *
     * @param success Runnable to execute on success
     */
    public Transaction onSuccess(Runnable success) {
        this.success = success;
        return this;
    }

    /**
     * Sets error callback
     *
     * @param error Consumer to handle exceptions
     */
    public Transaction onError(Consumer<Exception> error) {
        this.error = error;
        return this;
    }

    /**
     * Sets the transaction type to UPDATE
     */
    public Transaction update() {
        this.type = Type.UPDATE;
        return this;
    }

    /**
     * Sets the transaction type to INSERT
     */
    public Transaction insert() {
        this.type = Type.INSERT;
        return this;
    }

    /**
     * Sets the transaction type to DELETE
     */
    public Transaction delete() {
        this.type = Type.DELETE;
        return this;
    }

    /**
     * Queues this transaction for execution
     */
    public Transaction queue() {
        XG7PluginsAPI.database().getProcessor().queueTransaction(this);
        return this;
    }

    /**
     * Enum representing different types of database operations.
     * Provides methods to build appropriate SQL queries based on the operation type.
     */
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


    /**
     * Waits for the transaction to complete.
     * Queues the transaction and blocks until it is finished.
     *
     * @throws RuntimeException if interrupted while waiting
     */
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

    /**
     * Marks this transaction as complete.
     * Releases any threads waiting on waitForResult().
     */
    public void completeTask() {
        latch.countDown();
    }
}
