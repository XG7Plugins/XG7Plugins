package com.xg7plugins.data.database.query;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.entity.*;
import com.xg7plugins.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Represents a SQL query builder with support for complex queries including joins, conditions, and nested entities.
 * This class provides a fluent interface for building and executing SQL queries.
 */
public class Query {

    private final String initialTable;
    private final List<Pair<JoinType, String>> joinTables;
    private final List<Pair<String, List<String>>> columns = new ArrayList<>();
    private boolean selectAll = false;
    @Getter
    private final List<Object> params = new ArrayList<>();
    private String where;
    private final HashMap<String, String> conditions = new HashMap<>();
    private String additionalCommands;

    private QueryResult finishedResult;

    @Getter
    private Consumer<Exception> error;

    @Getter
    private Consumer<QueryResult> result;

    @Getter
    private final Plugin plugin;

    @Getter
    private String query;

    public Query(Plugin plugin, String table) {
        this.plugin = plugin;
        this.initialTable = table;
        this.joinTables = new ArrayList<>();
    }

    /**
     * Creates a new Query instance for selecting from a specified table.
     *
     * @param plugin The plugin instance making the query
     * @param table  The name of the table to select from
     * @return A new Query instance
     */
    public synchronized static Query selectFrom(Plugin plugin, String table) {
        return new Query(plugin, table);
    }

    /**
     * Creates a new Query instance for selecting an entity by its ID.
     * Automatically configures the query with all columns and handles nested entity relationships.
     *
     * @param plugin      The plugin instance making the query
     * @param entityClass The entity class to query
     * @param id          The ID value to search for
     * @return A new Query instance configured for the entity lookup
     * @throws IllegalStateException if no primary key is found in the entity class
     */
    public synchronized static Query selectFrom(Plugin plugin, Class<? extends Entity> entityClass, Object id) {

        Objects.requireNonNull(id, "ID cannot be null");

        String initialTable = entityClass.isAnnotationPresent(Table.class) ?
                entityClass.getAnnotation(Table.class).name() :
                entityClass.getSimpleName();

        Query query = new Query(plugin, initialTable);
        query.allColumns();

        String idColumn = null;

        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);

            if (!field.isAnnotationPresent(Pkey.class)) continue;

            idColumn = field.isAnnotationPresent(Column.class) ?
                    field.getAnnotation(Column.class).name() :
                    field.getName();

            break;
        }

        if (idColumn == null) {
            throw new IllegalStateException("Primary key not found for " + entityClass.getSimpleName());
        }

        selectNestedLists(query, entityClass, idColumn);

        query.where(initialTable + "." + idColumn + " = ?");
        query.params(id);

        return query;

    }

    public synchronized static Query selectAllFrom(Plugin plugin, Class<? extends Entity> entityClass) {
        String initialTable = entityClass.isAnnotationPresent(Table.class) ?
                entityClass.getAnnotation(Table.class).name() :
                entityClass.getSimpleName();

        Query query = new Query(plugin, initialTable);
        query.allColumns();

        selectNestedLists(query, entityClass, getPrimaryKeyColumn(entityClass));

        return query;
    }

    private static String getPrimaryKeyColumn(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Pkey.class)) {
                return field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name() : field.getName();
            }
        }
        throw new IllegalStateException("Primary key not found for " + clazz.getSimpleName());
    }

    /**
     * Recursively processes nested entity collections and adds the necessary joins to the query.
     * Handles foreign key relationships and builds the appropriate JOIN clauses.
     *
     * @param query     The query to modify
     * @param listClass The class containing nested collections
     * @param localId   The ID column of the parent entity
     */
    private static void selectNestedLists(Query query, Class<?> listClass, String localId) {
        for (Field nestedField : listClass.getDeclaredFields()) {
            nestedField.setAccessible(true);

            if (!Collection.class.isAssignableFrom(nestedField.getType())) continue;

            ParameterizedType nestedType = (ParameterizedType) nestedField.getGenericType();
            Class<?> nestedListClass = (Class<?>) nestedType.getActualTypeArguments()[0];

            String tableName = listClass.isAnnotationPresent(Table.class) ?
                    listClass.getAnnotation(Table.class).name() :
                    listClass.getSimpleName();

            String listTableName = nestedListClass.isAnnotationPresent(Table.class) ?
                    nestedListClass.getAnnotation(Table.class).name() :
                    nestedListClass.getSimpleName();

            query.leftJoin(listTableName);

            String idColumn = null;
            String fkeyColumn = null;

            for (Field field : nestedListClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(FKey.class)) {
                    fkeyColumn = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name() : field.getName();
                    continue;
                }

                if (field.isAnnotationPresent(Pkey.class)) idColumn = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name() : field.getName();


            }

            if (idColumn == null) throw new IllegalStateException("Primary key not found for " + listTableName);


            query.on(tableName + "." + localId + " = " + listTableName + "." + fkeyColumn);

            selectNestedLists(query, nestedListClass, idColumn);

        }
    }

    /**
     * Configures the query to select all columns using SELECT *.
     *
     * @return This query instance for method chaining
     */
    public Query allColumns() {
        this.selectAll = true;
        return this;
    }

    /**
     * Specifies which columns to select in the query.
     *
     * @param columns Array of pairs containing table names and their corresponding column lists
     * @return This query instance for method chaining
     */
    public Query columns(Pair<String, List<String>>... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    /**
     * Adds an INNER JOIN clause to the query.
     *
     * @param table The name of the table to join
     * @return This query instance for method chaining
     */
    public Query innerJoin(String table) {
        this.joinTables.add(new Pair<>(JoinType.INNER_JOIN, table));
        return this;
    }

    /**
     * Adds a LEFT JOIN clause to the query.
     *
     * @param table The name of the table to join
     * @return This query instance for method chaining
     */
    public Query leftJoin(String table) {
        this.joinTables.add(new Pair<>(JoinType.LEFT_JOIN, table));
        return this;
    }

    /**
     * Adds a RIGHT JOIN clause to the query.
     *
     * @param table The name of the table to join
     * @return This query instance for method chaining
     */
    public Query rightJoin(String table) {
        this.joinTables.add(new Pair<>(JoinType.RIGHT_JOIN, table));
        return this;
    }

    /**
     * Adds a FULL JOIN clause to the query.
     *
     * @param table The name of the table to join
     * @return This query instance for method chaining
     */
    public Query fullJoin(String table) {
        this.joinTables.add(new Pair<>(JoinType.FULL_JOIN, table));
        return this;
    }


    /**
     * Adds additional SQL commands to the end of the query.
     *
     * @param additionalCommands The SQL commands to append
     * @return This query instance for method chaining
     */
    public Query additionalCommands(String additionalCommands) {
        this.additionalCommands = additionalCommands;
        return this;
    }

    /**
     * Adds an ON clause for the most recently added join.
     *
     * @param condition The join condition
     * @return This query instance for method chaining
     */
    public Query on(String condition) {
        this.conditions.put(this.joinTables.get(this.joinTables.size() - 1).getSecond(), condition);
        return this;
    }

    /**
     * Sets the WHERE clause of the query.
     *
     * @param condition The WHERE condition
     * @return This query instance for method chaining
     */
    public Query where(String condition) {
        this.where = condition;
        return this;
    }

    /**
     * Adds parameters to be used in the prepared statement.
     *
     * @param params The parameter values
     * @return This query instance for method chaining
     */
    public Query params(Object... params) {
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    /**
     * Sets the error handler for the query execution.
     *
     * @param error The error handler callback
     * @return This query instance for method chaining
     */
    public Query onError(Consumer<Exception> error) {
        this.error = error;
        return this;
    }

    /**
     * Builds the final SQL query string and queues it for execution.
     * Combines all configured parts of the query including joins, conditions, and parameters.
     *
     * @return This query instance for method chaining
     */
    public QueryResult process() throws Exception {
        StringBuilder query = new StringBuilder("SELECT ");

        if (this.selectAll) {
            query.append("*");
        } else {
            boolean firstColumn = true;
            for (Pair<String, List<String>> entry : this.columns) {
                String table = entry.getFirst();
                List<String> columns = entry.getSecond();

                for (String column : columns) {
                    if (!firstColumn) query.append(", ");
                    query.append(table).append(".").append(column);
                    firstColumn = false;
                }
            }
        }

        query.append(" FROM ").append(initialTable);

        for (Pair<JoinType, String> table : this.joinTables) {
            query.append(" ").append(table.getFirst().getSql()).append(" ").append(table.getSecond());
            if (conditions.containsKey(table.getSecond())) {
                query.append(" ON ").append(conditions.get(table.getSecond()));
            }
        }

        if (this.where != null && !this.where.isEmpty()) {
            query.append(" WHERE ").append(this.where);
        }

        if (this.additionalCommands != null && !this.additionalCommands.isEmpty()) {
            query.append(" ").append(additionalCommands);
        }

        this.query = query.toString();

        return XG7PluginsAPI.dbProcessor().processQuery(this);
    }


    /**
     * Enum representing different SQL join types.
     * Used to specify how tables should be joined in SQL queries.
     */
    @Getter
    @AllArgsConstructor
    enum JoinType {
        INNER_JOIN("INNER JOIN"),
        LEFT_JOIN("LEFT JOIN"),
        RIGHT_JOIN("RIGHT JOIN"),
        FULL_JOIN("FULL JOIN");

        private final String sql;
    }




}
