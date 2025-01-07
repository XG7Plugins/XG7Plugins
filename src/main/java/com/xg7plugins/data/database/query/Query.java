package com.xg7plugins.data.database.query;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.entity.*;
import com.xg7plugins.utils.Pair;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class Query {

    private final String initialTable;
    private final List<String> joinTables;
    private final List<Pair<String, List<String>>> columns = new ArrayList<>();
    private boolean selectAll = false;
    @Getter
    private final List<Object> params = new ArrayList<>();
    private String where;
    private final HashMap<String, String> conditions = new HashMap<>();
    private String additionalCommands;

    private final CountDownLatch latch = new CountDownLatch(1);
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

    public static Query selectFrom(Plugin plugin, String table) {
        return new Query(plugin, table);
    }

    public static Query selectFrom(Plugin plugin, Class<? extends Entity> entityClass, Object id) {

        String initialTable = entityClass.isAnnotationPresent(Table.class) ?
                entityClass.getAnnotation(Table.class).name() :
                entityClass.getSimpleName();

        Query query = new Query(plugin, initialTable);

        query.allColumns();

        String idColumn = null;

        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);

            if (!field.isAnnotationPresent(Pkey.class)) continue;

            idColumn = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name() : field.getName();

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

            query.innerJoin(listTableName);

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

    public Query allColumns() {
        this.selectAll = true;
        return this;
    }

    public Query columns(Pair<String, List<String>>... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    public Query innerJoin(String table) {
        this.joinTables.add(table);
        return this;
    }
    public Query additionalCommands(String additionalCommands) {
        this.additionalCommands = additionalCommands;
        return this;
    }

    public Query on(String condition) {
        this.conditions.put(this.joinTables.get(this.joinTables.size() - 1), condition);
        return this;
    }

    public Query where(String condition) {
        this.where = condition;
        return this;
    }

    public Query params(Object... params) {
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    public Query onError(Consumer<Exception> error) {
        this.error = error;
        return this;
    }

    public Query onResult(Consumer<QueryResult> result) {
        this.result = result;
        return this;
    }

    public Query queue() {
        StringBuilder query = new StringBuilder("SELECT ");
        if (this.selectAll) query.append("*");
        else {
            for (Pair<String, List<String>> entry : this.columns) {
                String table = entry.getFirst();

                List<String> columns = entry.getSecond();

                for (int i = 0; i < columns.size(); i++) {
                    query.append(table).append(".").append(columns.get(i));
                    if (i != columns.size() - 1) query.append(", ");
                }
            }
        }

        query.append(" FROM ").append(initialTable);

        for (String table : this.joinTables) {
            query.append(" INNER JOIN ").append(table);
            if (conditions.containsKey(table)) {
                query.append(" ON ");
                query.append(conditions.get(table));
            }

        }

        if (this.where != null && !this.where.isEmpty()) query.append(" WHERE ").append(this.where);

        if (this.additionalCommands != null && !this.additionalCommands.isEmpty()) query.append(" ").append(additionalCommands);

        this.query = query.toString();

        XG7Plugins.getInstance().getDatabaseManager().getProcessor().queueQuery(this);

        return this;
    }


    public QueryResult waitForResult() {
        try {
            queue();
            latch.await();
            return finishedResult;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public void completeTask(QueryResult result) {
        this.finishedResult = result;
        latch.countDown();
    }




}
