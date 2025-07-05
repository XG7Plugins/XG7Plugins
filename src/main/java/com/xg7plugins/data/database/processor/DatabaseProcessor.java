package com.xg7plugins.data.database.processor;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.entity.Pkey;
import com.xg7plugins.data.database.entity.Table;
import com.xg7plugins.data.database.query.Query;
import com.xg7plugins.data.database.query.QueryResult;
import com.xg7plugins.data.database.query.Transaction;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.Pair;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Processes database operations asynchronously using queues and thread pools.
 * <p>
 * This class manages the execution of database queries and transactions,
 * handling them in separate queues and processing them asynchronously.
 * It provides methods to queue operations and checks their execution status.
 * </p>
 */
@AllArgsConstructor
public class DatabaseProcessor {

    private final DatabaseManager databaseManager;
    private final long timeout = Config.mainConfigOf(XG7Plugins.getInstance()).getTimeInMilliseconds("sql.connection-timeout").orElse(5000L);

    /**
     * Processes the next transaction in the queue if available.
     * <p>
     * Executes all SQL commands in the transaction as a single atomic unit,
     * committing if successful and rolling back if an error occurs.
     * </p>
     *
     * @throws Exception If an error occurs during transaction processing
     */
    public void processTransaction(Transaction transaction) throws Exception {

        Connection connection = databaseManager.getConnection(transaction.getPlugin());
        if (connection == null) return;

        PreparedStatement ps = null;
        String currentQuery = "";

        try {
            for (Pair<String, List<Object>> query : transaction.getQueries()) {
                currentQuery = query.getFirst();

                ps = connection.prepareStatement(currentQuery);
                ps.setQueryTimeout((int) (timeout / 1000));

                for (int i = 0; i < query.getSecond().size(); i++) {
                    Object o = query.getSecond().get(i);
                    if (o instanceof UUID) ps.setString(i + 1, o.toString());
                    else ps.setObject(i + 1, o);
                }

                ps.executeUpdate();
                ps.close();
            }

            connection.commit();

            if (transaction.getSuccess() != null) transaction.getSuccess().run();

        } catch (SQLException e) {
            Debug.of(XG7Plugins.getInstance()).severe("Error in processing query: " + currentQuery);
            Debug.of(XG7Plugins.getInstance()).severe("Error message: " + e.getMessage());

            try {
                connection.rollback();

                if (ps != null) ps.close();

                if (transaction.getError() != null) transaction.getError().accept(e);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Processes the next query in the queue if available.
     * <p>
     * Executes the SQL query, collects the results, and provides them to the
     * callback registered with the query.
     * </p>
     *
     * @throws Exception If an error occurs during query processing
     */
    public QueryResult processQuery(Query query) throws Exception {
        if (databaseManager == null) {
            System.err.println("databaseManager is null!");
            query.getError().accept(new NullPointerException("Database manager is null"));
            throw new NullPointerException("Database manager is null!");
        }
        Debug.of(XG7Plugins.getInstance()).info("Processing query: " + query.getQuery());

        Connection connection = databaseManager.getConnection(query.getPlugin());

        Debug.of(XG7Plugins.getInstance()).info("Connection: " + connection);

        if (connection == null) {
            System.err.println("Failed to get a database connection for plugin: " + query.getPlugin());
            query.getError().accept(new RuntimeException("Failed to get a database connection for plugin:" + query.getPlugin()));
            throw new RuntimeException("Failed to get a database connection for plugin: " + query.getPlugin());
        }

        try (PreparedStatement ps = connection.prepareStatement(query.getQuery())) {
            ps.setQueryTimeout((int) (timeout / 1000));
            for (int i = 0; i < query.getParams().size(); i++) {
                Object o = query.getParams().get(i);
                if (o instanceof UUID) ps.setString(i + 1, o.toString());
                else ps.setObject(i + 1, o);
            }

            Debug.of(XG7Plugins.getInstance()).info("Executing query: " + query.getQuery());
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();

                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        map.put(rs.getMetaData().getTableName(i) + "." + rs.getMetaData().getColumnName(i), rs.getObject(i));
                    }
                    results.add(map);
                }

                Debug.of(XG7Plugins.getInstance()).info("Query executed successfully: " + query.getQuery());
                Debug.of(XG7Plugins.getInstance()).info("Results: " + results);
                Debug.of(XG7Plugins.getInstance()).info("Making QueryResult");

                return new QueryResult(query.getPlugin(), results.iterator());
            }

        } catch (SQLException e) {
            System.err.println("Error while processing query: " + query.getQuery() + " | " + e.getMessage());
            query.getError().accept(new RuntimeException("Error while processing query: " + query.getQuery()));
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if an entity with the given ID exists in the database.
     * <p>
     * First checks the entity cache, then performs a database lookup if necessary.
     * </p>
     *
     * @param plugin The plugin requesting the check
     * @param table The entity class to check
     * @param id The ID value to look for
     * @return A CompletableFuture that resolves to true if the entity exists
     */
    public boolean exists(Plugin plugin, Class<? extends Entity> table, Object id) {
        if (databaseManager.containsCachedEntity(plugin, id.toString()).join()) return true;

        Connection connection;
        try {
            connection = databaseManager.getConnection(plugin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (connection == null) return false;

        String idCol = getPrimaryKeyColumnName(table);

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM " + (table.isAnnotationPresent(Table.class) ? table.getAnnotation(Table.class).name() : table.getSimpleName()) + " WHERE " + idCol + " = ?");
            if (UUID.class.isAssignableFrom(id.getClass())) ps.setString(1, id.toString());
            else ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            ps.close();
            return exists;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPrimaryKeyColumnName(Class<?> table) {
        for (Field field : table.getDeclaredFields()) {
            if (field.isAnnotationPresent(Pkey.class)) {
                if (field.isAnnotationPresent(Column.class)) {
                    return field.getAnnotation(Column.class).name();
                } else {
                    return field.getName();
                }
            }
        }
        return null;
    }
}