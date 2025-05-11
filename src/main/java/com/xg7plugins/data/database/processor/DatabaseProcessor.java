package com.xg7plugins.data.database.processor;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.entity.Table;
import com.xg7plugins.data.database.query.Query;
import com.xg7plugins.data.database.query.QueryResult;
import com.xg7plugins.data.database.query.Transaction;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.Pair;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DatabaseProcessor {

    private final DatabaseManager databaseManager;
    private final long timeout = Config.mainConfigOf(XG7Plugins.getInstance()).getTime("sql.connection-timeout").orElse(5000L);

    @Getter
    private final ScheduledExecutorService executorService;

    private final Queue<Transaction> transactionQueue = new LinkedList<>();
    private final Queue<Query> queryQueue = new LinkedList<>();

    public DatabaseProcessor(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;

        Config config = Config.mainConfigOf(XG7Plugins.getInstance());

        this.executorService = Executors.newScheduledThreadPool(config.get("sql.query-processor-threads", Integer.class).orElse(3));
        process(config.getTime("sql.sql-command-processing-interval").orElse(20L));
    }


    public void queueTransaction(Transaction transaction) {
        transactionQueue.add(transaction);
    }

    public void queueQuery(Query query) {
        queryQueue.add(query);
    }

    public void process(long delay) {
        executorService.scheduleWithFixedDelay(() -> {
            try {
                processTransaction();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, 0, delay, TimeUnit.MILLISECONDS);
        executorService.scheduleWithFixedDelay(() -> {
            try {
                processQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, 0, delay, TimeUnit.MILLISECONDS);
    }

    private void processTransaction() throws SQLException {
        if (transactionQueue.isEmpty()) return;

        Transaction transaction = transactionQueue.poll();

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

            transaction.completeTask();

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

            transaction.completeTask();

            throw new RuntimeException(e);
        }
    }
    private void processQuery() throws SQLException {
        if (databaseManager == null) {
            System.err.println("databaseManager is null!");
            return;
        }

        if (queryQueue.isEmpty()) return;

        Query query = queryQueue.poll();

        Debug.of(XG7Plugins.getInstance()).info("Processing query: " + query.getQuery());

        Connection connection = databaseManager.getConnection(query.getPlugin());

        Debug.of(XG7Plugins.getInstance()).info("Connection: " + connection);

        if (connection == null) {
            System.err.println("Failed to get a database connection for plugin: " + query.getPlugin());
            return;
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
                QueryResult result = new QueryResult(query.getPlugin(), results.iterator());
                if (query.getResult() != null) query.getResult().accept(result);

                Debug.of(XG7Plugins.getInstance()).info("Completing task");
                query.completeTask(result);
            }

        } catch (SQLException e) {
            System.err.println("Error while processing query: " + query.getQuery() + " | " + e.getMessage());
            query.completeTask(new QueryResult(query.getPlugin(), null));
            throw new RuntimeException(e);
        }

    }

    public void shutdown() throws SQLException {
        executorService.shutdown();

        while (!queryQueue.isEmpty()) {
            processQuery();
        }
        while (!transactionQueue.isEmpty()) {
            processTransaction();
        }

        executorService.shutdownNow();

    }

    public CompletableFuture<Boolean> exists(Plugin plugin, Class<? extends Entity> table, String idCol, Object id) {
        return CompletableFuture.supplyAsync(() -> {
            if (databaseManager.containsCachedEntity(plugin, id.toString()).join()) return true;

            Connection connection;
            try {
                connection = databaseManager.getConnection(plugin);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (connection == null) return false;

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
        }, executorService);
    }

}
