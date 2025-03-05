package com.xg7plugins.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CacheManager {

    private JedisPool pool;
    private final Gson gson = new Gson();

    private boolean cacheExpires = true;

    private final HashMap<String, Cache<@NotNull Object, Object>> caches;

    public CacheManager(XG7Plugins plugin) {

        Config config = plugin.getConfigsManager().getConfig("config");

        this.caches = new HashMap<>();

        if (config.get("redis-cache.enabled", Boolean.class).orElse(false)) {

            this.cacheExpires = config.get("redis-cache.cache-expires", Boolean.class).orElse(true);

            String host = config.get("redis-cache.host", String.class).orElse(null);
            int port = config.get("redis-cache.port", Integer.class).orElse(0);

            boolean authEnabled = config.get("redis-cache.user-auth-enabled", Boolean.class).orElse(false);

            String user = config.get("redis-cache.username", String.class).orElse(null);
            String password = config.get("redis-cache.password", String.class).orElse(null);
            try {
                pool = authEnabled ? new JedisPool(host, port, user, password) : new JedisPool(host,port);

                int minIdle = config.get("redis-cache.min-idle-connections", Integer.class).orElse(10);
                int maxIdle = config.get("redis-cache.max-idle-connections", Integer.class).orElse(50);
                int maxPoolSize = config.get("redis-cache.max-connections", Integer.class).orElse(100);

                long timeout = config.getTime("redis-cache.max-wait-time").orElse(1000L);

                pool.setMinIdle(minIdle);
                pool.setMaxIdle(maxIdle);
                pool.setMaxTotal(maxPoolSize);
                pool.setMaxWaitMillis(timeout);

                Jedis jedis = pool.getResource();
                if (authEnabled) jedis.auth(password);
                jedis.close();
            } catch (Exception e) {
                plugin.getDebug().severe("Failed to connect to Redis: " + e.getMessage());
                plugin.getDebug().severe("Using local cache instead.");
            }
        }


    }

    public void registerCache(ObjectCache<?,?> cache) {
        if (pool == null || cache.isUseLocalCache()) {

            Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();

            if (cache.getExpireTime() > 0) {
                if (cache.isExpiresAfterWrite()) cacheBuilder.expireAfterWrite(cache.getExpireTime(), TimeUnit.MILLISECONDS);
                else cacheBuilder.expireAfterAccess(cache.getExpireTime(), TimeUnit.MILLISECONDS);
            }

            caches.put(cache.getPlugin().getName() + ":" + cache.getName(), cacheBuilder.build());
        }
    }

    public void shutdown() {
        if (pool != null) {
            pool.close();
        }
    }

    public CompletableFuture<Void> cacheObject(ObjectCache<?,?> cache, Object key, Object value) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.runAsync(() -> {
                try (Jedis jedis = pool.getResource()) {
                    jedis.hset(cache.getPlugin() + ":" + cache.getName(), gson.toJson(key), gson.toJson(value));
                    if (cacheExpires) jedis.hexpire(cache.getPlugin().getName() + ":" + cache.getName(), cache.getExpireTime() / 1000, key.toString());
                }

            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));
        }

        return CompletableFuture.runAsync(() -> caches.get(cache.getPlugin().getName() + ":" + cache.getName()).put(key, value));
    }

    public <T> CompletableFuture<T> getCachedObject(ObjectCache<?,T> cache, Object key) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.supplyAsync(() -> {
                try (Jedis jedis = pool.getResource()) {
                    return (T) gson.fromJson(jedis.hget(cache.getPlugin().getName() + ":" + cache.getName(), gson.toJson(key)), Object.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));

        }

        return CompletableFuture.completedFuture((T) caches.get(cache.getPlugin().getName() + ":" + cache.getName()).getIfPresent(key));
    }

    public CompletableFuture<Void> removeCachedObject(ObjectCache<?,?> cache, Object key) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.runAsync(() -> {
                try (Jedis jedis = pool.getResource()) {
                    jedis.hdel(cache.getPlugin().getName() + ":" + cache.getName(), gson.toJson(key));
                    if (jedis.hlen(cache.getPlugin().getName() + ":" + cache.getName()) == 0) clearCache(cache);
                }
            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));
        }

        return CompletableFuture.runAsync(() -> caches.get(cache.getPlugin().getName() + ":" + cache.getName()).invalidate(key));
    }

    public CompletableFuture<Void> clearCache(ObjectCache<?,?> cache) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.runAsync(() -> {
                try (Jedis jedis = pool.getResource()) {
                    jedis.del(cache.getPlugin().getName() + ":" + cache.getName());
                }
            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));
        }

        return CompletableFuture.runAsync(() -> caches.get(cache.getPlugin().getName() + ":" + cache.getName()).invalidateAll());
    }

    public CompletableFuture<Boolean> containsKey(ObjectCache<?,?> cache, Object key) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.supplyAsync(() -> {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.hexists(cache.getPlugin().getName() + ":" + cache.getName(), gson.toJson(key));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));
        }

        return CompletableFuture.completedFuture(caches.get(cache.getPlugin().getName() + ":" + cache.getName()).getIfPresent(key) != null);
    }

    public <K,V> CompletableFuture<Map<K, V>> getMap(ObjectCache<K,V> cache) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.supplyAsync(() -> {
                try (Jedis jedis = pool.getResource()) {
                    Map<String, String> redisMap = jedis.hgetAll(cache.getPlugin().getName() + ":" + cache.getName());

                    return redisMap.entrySet().stream()
                            .collect(Collectors.toMap(
                                    entry -> gson.fromJson(entry.getKey(), cache.getKeyType()),
                                    entry -> gson.fromJson(entry.getValue(), cache.getValueType())
                            ));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));
        }

        return CompletableFuture.completedFuture((Map<K, V>) caches.get(cache.getPlugin().getName() + ":" + cache.getName()).asMap());
    }







}
