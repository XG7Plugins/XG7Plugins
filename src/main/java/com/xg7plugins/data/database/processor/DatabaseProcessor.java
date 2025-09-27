package com.xg7plugins.data.database.processor;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.data.database.ConnectionType;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.data.database.connector.SQLConfigs;
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
 * Processes database operations with support for synchronous and asynchronous execution.
 * <p>
 * This class handles database queries and transactions execution,
 * with asynchronous operations being configured in other parts of the code.
 * It provides methods for direct database operations and checks their execution status.
 * </p>
 */
@AllArgsConstructor
public class DatabaseProcessor {

    private final DatabaseManager databaseManager;

    private long timeout() {
        return ConfigFile.mainConfigOf(XG7Plugins.getInstance()).section("sql").getTimeInMilliseconds("connection-timeout", 5000L);
    }

    /**
     * Processes a database transaction.
     * <p>
     * Executes all SQL commands in the transaction as a single atomic unit,
     * committing if successful and rolling back if an error occurs.
     * </p>
     *
     * @param transaction The transaction to process
     * @throws Exception If an error occurs during transaction processing
     */
    public void processTransaction(Transaction transaction) throws Exception {
        Debug.of(XG7Plugins.getInstance()).info("Processing transaction");

        if (databaseManager == null) {
            NullPointerException ex = new NullPointerException("Database manager is null");
            Debug.of(XG7Plugins.getInstance()).severe(ex.getMessage());
            transaction.getError().accept(ex);
            throw ex;
        }

        Connection connection = null;

        try {
            connection = databaseManager.getConnection(transaction.getPlugin());

            if (connection == null) {
                Debug.of(XG7Plugins.getInstance()).severe("Failed to get database connection");
                return;
            }

            PreparedStatement ps = null;
            String currentQuery = "";

            try {
                Debug.of(XG7Plugins.getInstance()).info("Starting transaction with " + transaction.getQueries().size() + " queries");

                for (Pair<String, List<Object>> query : transaction.getQueries()) {
                    currentQuery = query.getFirst();
                    Debug.of(XG7Plugins.getInstance()).info("Executing query: " + currentQuery);

                    ps = connection.prepareStatement(currentQuery);
                    ps.setQueryTimeout((int) (timeout() / 1000));

                    for (int i = 0; i < query.getSecond().size(); i++) {
                        Object o = query.getSecond().get(i);
                        if (o instanceof UUID) ps.setString(i + 1, o.toString());
                        else ps.setObject(i + 1, o);
                    }

                    ps.executeUpdate();
                    Debug.of(XG7Plugins.getInstance()).info("Query executed successfully");
                    ps.close();
                }

                connection.commit();
                Debug.of(XG7Plugins.getInstance()).info("Transaction committed");

                if (transaction.getSuccess() != null) {
                    transaction.getSuccess().run();
                    Debug.of(XG7Plugins.getInstance()).info("Success callback executed");
                }

            } catch (SQLException e) {
                Debug.of(XG7Plugins.getInstance()).severe("Error in processing query: " + currentQuery);
                Debug.of(XG7Plugins.getInstance()).severe("Error message: " + e.getMessage());
                e.printStackTrace();

                try {
                    connection.rollback();
                    Debug.of(XG7Plugins.getInstance()).info("Transaction rolled back");

                    if (ps != null) ps.close();

                    if (transaction.getError() != null) {
                        transaction.getError().accept(e);
                        Debug.of(XG7Plugins.getInstance()).info("Error callback executed");
                    }
                } catch (SQLException ex) {
                    Debug.of(XG7Plugins.getInstance()).severe("Error in rollback: " + ex.getMessage());
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null && !SQLConfigs.of(transaction.getPlugin()).getConnectionType().equals(ConnectionType.SQLITE)) {
                connection.close();
            }
        }
    }

    /**
     * Processes a database query.
     * <p>
     * Executes the SQL query, collects the results, and returns them
     * as a QueryResult object.
     * </p>
     *
     * @param query The query to process
     * @return The query results
     * @throws Exception If an error occurs during query processing
     */
    public QueryResult processQuery(Query query) throws Exception {
        Debug.of(XG7Plugins.getInstance()).info("Starting query processing");

        if (databaseManager == null) {
            NullPointerException ex = new NullPointerException("Database manager is null");
            Debug.of(XG7Plugins.getInstance()).severe(ex.getMessage());
            query.getError().accept(ex);
            throw ex;
        }
        Debug.of(XG7Plugins.getInstance()).info("Processing query: " + query.getQuery());

        Connection connection = null;

        try {
            connection = databaseManager.getConnection(query.getPlugin());

            Debug.of(XG7Plugins.getInstance()).info("Connection: " + connection);

            if (connection == null) {
                RuntimeException ex = new RuntimeException("Failed to get a database connection for plugin:" + query.getPlugin());
                Debug.of(XG7Plugins.getInstance()).severe(ex.getMessage());
                query.getError().accept(ex);
                throw ex;
            }

            try (PreparedStatement ps = connection.prepareStatement(query.getQuery())) {
                ps.setQueryTimeout((int) (timeout() / 1000));
                Debug.of(XG7Plugins.getInstance()).info("Setting " + query.getParams().size() + " parameters");

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
                            String key = rs.getMetaData().getTableName(i) + "." + rs.getMetaData().getColumnName(i);
                            Object value = rs.getObject(i);
                            map.put(key, value);
                        }
                        results.add(map);
                    }

                    Debug.of(XG7Plugins.getInstance()).info("Query executed successfully: " + query.getQuery());
                    Debug.of(XG7Plugins.getInstance()).info("Results: " + results);
                    Debug.of(XG7Plugins.getInstance()).info("Making QueryResult");

                    return new QueryResult(query.getPlugin(), results.iterator());
                }

            } catch (SQLException e) {
                Debug.of(XG7Plugins.getInstance()).severe("Error while processing query: " + query.getQuery());
                Debug.of(XG7Plugins.getInstance()).severe("Error message: " + e.getMessage());
                e.printStackTrace();

                query.getError().accept(new RuntimeException("Error while processing query: " + query.getQuery()));
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null && !SQLConfigs.of(query.getPlugin()).getConnectionType().equals(ConnectionType.SQLITE)) {
                connection.close();
            }
        }
    }

    /**
     * Checks if an entity with the given ID exists in the database.
     * <p>
     * First checks the entity cache, then performs a direct database lookup if necessary.
     * </p>
     *
     * @param plugin The plugin requesting the check
     * @param table  The entity class to check
     * @param id     The ID value to look for
     * @return true if the entity exists, false otherwise
     */
    public boolean exists(Plugin plugin, Class<? extends Entity> table, Object id) {
        Debug.of(XG7Plugins.getInstance()).info("Checking existence for " + table.getSimpleName() + " with ID " + id);

        if (databaseManager.containsCachedEntity(plugin, id.toString()).join()) {
            Debug.of(XG7Plugins.getInstance()).info("Entity found in cache");
            return true;
        }

        Connection connection;
        try {
            connection = databaseManager.getConnection(plugin);
        } catch (Exception e) {
            Debug.of(XG7Plugins.getInstance()).severe("Error getting database connection: " + e.getMessage());
            throw new RuntimeException(e);
        }

        if (connection == null) {
            Debug.of(XG7Plugins.getInstance()).severe("Failed to get database connection");
            return false;
        }

        String idCol = getPrimaryKeyColumnName(table);
        String tableName = table.isAnnotationPresent(Table.class) ? table.getAnnotation(Table.class).name() : table.getSimpleName();
        String sql = "SELECT 1 FROM " + tableName + " WHERE " + idCol + " = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            if (UUID.class.isAssignableFrom(id.getClass())) ps.setString(1, id.toString());
            else ps.setObject(1, id);

            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            ps.close();

            if (!SQLConfigs.of(plugin).getConnectionType().equals(ConnectionType.SQLITE)) {
                connection.close();
            }
            Debug.of(XG7Plugins.getInstance()).info("Entity exists: " + exists);
            return exists;

        } catch (SQLException e) {
            Debug.of(XG7Plugins.getInstance()).severe("Error checking existence: " + e.getMessage());
            e.printStackTrace();
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