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
    private final long timeout = Config.mainConfigOf(XG7Plugins.getInstance()).getTime("sql.timeout").orElse(5000L);

    @Getter
    private final ScheduledExecutorService executorService;

    private final Queue<Transaction> transactionQueue = new LinkedList<>();
    private final Queue<Query> queryQueue = new LinkedList<>();

    public DatabaseProcessor(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.executorService = Executors.newScheduledThreadPool(XG7Plugins.getInstance().getConfig("config").get("sql.query-processor-threads", Integer.class).orElse(3));
        process(XG7Plugins.getInstance().getConfig("config").getTime("sql.sql-command-processing-interval").orElse(20L));
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
        System.out.println("[DEBUG] Processando transação: " + transaction);

        Connection connection = databaseManager.getConnection(transaction.getPlugin());
        if (connection == null) {
            System.out.println("[DEBUG] Conexão com o banco de dados não pôde ser obtida para o plugin: " + transaction.getPlugin().getName());
            return;
        }

        PreparedStatement ps = null;
        String currentQuery = "";

        try {
            System.out.println("[DEBUG] Iniciando execução das queries da transação.");

            for (Pair<String, List<Object>> query : transaction.getQueries()) {
                currentQuery = query.getFirst();
                System.out.println("[DEBUG] Preparando query: " + currentQuery);
                System.out.println("[DEBUG] Parâmetros da query: " + query.getSecond());

                ps = connection.prepareStatement(currentQuery);
                ps.setQueryTimeout((int) (timeout / 1000));

                // Configura os parâmetros da query
                for (int i = 0; i < query.getSecond().size(); i++) {
                    Object o = query.getSecond().get(i);
                    if (o instanceof UUID) {
                        ps.setString(i + 1, o.toString());
                        System.out.println("[DEBUG] Parâmetro " + (i + 1) + ": UUID = " + o);
                    } else {
                        ps.setObject(i + 1, o);
                        System.out.println("[DEBUG] Parâmetro " + (i + 1) + ": " + o);
                    }
                }

                // Executa a query
                System.out.println("[DEBUG] Executando query: " + currentQuery);
                ps.executeUpdate();
                System.out.println("[DEBUG] Query executada com sucesso: " + currentQuery);

                // Fecha o PreparedStatement
                ps.close();
                System.out.println("[DEBUG] PreparedStatement fechado para a query: " + currentQuery);
            }

            // Commit da transação
            System.out.println("[DEBUG] Commit da transação.");
            connection.commit();
            System.out.println("[DEBUG] Commit realizado com sucesso.");

            // Executa o callback de sucesso, se existir
            if (transaction.getSuccess() != null) {
                System.out.println("[DEBUG] Executando callback de sucesso.");
                transaction.getSuccess().run();
            }

            // Marca a transação como concluída
            transaction.completeTask();
            System.out.println("[DEBUG] Transação concluída com sucesso.");

        } catch (SQLException e) {
            System.err.println("[DEBUG] Erro ao processar a query: " + currentQuery);
            System.err.println("[DEBUG] Mensagem de erro: " + e.getMessage());

            try {
                // Rollback em caso de erro
                System.out.println("[DEBUG] Realizando rollback da transação.");
                connection.rollback();
                System.out.println("[DEBUG] Rollback realizado com sucesso.");

                // Fecha o PreparedStatement, se ainda estiver aberto
                if (ps != null) {
                    System.out.println("[DEBUG] Fechando PreparedStatement após erro.");
                    ps.close();
                }

                // Executa o callback de erro, se existir
                if (transaction.getError() != null) {
                    System.out.println("[DEBUG] Executando callback de erro.");
                    transaction.getError().accept(e);
                }
            } catch (SQLException ex) {
                System.err.println("[DEBUG] Erro ao realizar rollback ou fechar PreparedStatement: " + ex.getMessage());
                throw new RuntimeException(ex);
            }

            // Marca a transação como concluída (mesmo em caso de erro)
            transaction.completeTask();
            System.out.println("[DEBUG] Transação marcada como concluída (com erro).");

            throw new RuntimeException(e);
        }
    }
    private void processQuery() throws SQLException {
        if (queryQueue == null || databaseManager == null) {
            System.err.println("queryQueue or databaseManager is null!");
            return;
        }

        if (queryQueue.isEmpty()) return;

        Query query = queryQueue.poll();

        Connection connection = databaseManager.getConnection(query.getPlugin());

        if (connection == null) {
            System.err.println("Failed to get a database connection for plugin: " + query.getPlugin());
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(query.getQuery())) {
            ps.setQueryTimeout((int) (timeout / 1000));
            for (int i = 0; i < query.getParams().size(); i++) {
                Object o = query.getParams().get(i);
                if (o instanceof UUID) {
                    ps.setString(i + 1, o.toString());
                } else {
                    ps.setObject(i + 1, o);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();

                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        map.put(rs.getMetaData().getTableName(i) + "." + rs.getMetaData().getColumnName(i), rs.getObject(i));
                    }
                    results.add(map);
                }

                QueryResult result = new QueryResult(query.getPlugin(), results.iterator());
                if (query.getResult() != null) query.getResult().accept(result);

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
