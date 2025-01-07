package com.xg7plugins.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CacheManager {

    private JedisPool pool;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private boolean cacheExpires = true;

    private HashMap<String, Cache<Object, Object>> caches;

    public CacheManager(XG7Plugins plugin) {

        Config config = plugin.getConfigsManager().getConfig("config");

        if (config.get("redis-cache.enabled", Boolean.class).orElse(false)) {

            this.cacheExpires = config.get("redis-cache.expires", Boolean.class).orElse(true);

            String host = config.get("redis-cache.host", String.class).orElse(null);
            int port = config.get("redis-cache.port", Integer.class).orElse(0);

            String user = config.get("redis-cache.user", String.class).orElse(null);
            String password = config.get("redis-cache.password", String.class).orElse(null);
            try {
                pool = config.get("redis-cache.user-auth-enabled", Boolean.class).orElse(false) ? new JedisPool(host,port) : new JedisPool(host, port, user, password);

                int minIdle = config.get("redis-cache.min-idle-connections", Integer.class).orElse(1);
                int maxIdle = config.get("redis-cache.max-idle-connections", Integer.class).orElse(5);
                int maxPoolSize = config.get("redis-cache.max-connections", Integer.class).orElse(10);

                long timeout = config.get("redis-cache.max-wait-time", Long.class).orElse(1000L);

                pool.setMinIdle(minIdle);
                pool.setMaxIdle(maxIdle);
                pool.setMaxTotal(maxPoolSize);
                pool.setMaxWaitMillis(timeout);

                Jedis jedis = pool.getResource();
                jedis.auth(password);
                jedis.close();
            } catch (Exception e) {
                plugin.getLog().severe("Failed to connect to Redis: " + e.getMessage());
                plugin.getLog().severe("Using local cache instead.");
            }
        }

        this.caches = new HashMap<>();

    }

    public void registerCache(ObjectCache cache) {
        if (caches != null && cache.isUseLocalCache()) {

            CacheBuilder<Object, Object> caffeine = CacheBuilder.newBuilder();

            if (cache.getExpireTime() > 0) {
                if (cache.isExpiresAfterWrite()) caffeine.expireAfterWrite(cache.getExpireTime(), TimeUnit.MILLISECONDS);
                else caffeine.expireAfterAccess(cache.getExpireTime(), TimeUnit.MILLISECONDS);
            }

            caches.put(cache.getPlugin().getName() + ":" + cache.getName(), caffeine.build());
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
                Jedis jedis = pool.getResource();
                jedis.hset(cache.getPlugin() + ":" + cache.getName(), gson.toJson(key), gson.toJson(value));
                if (cacheExpires) jedis.hexpire(cache.getPlugin().getName() + ":" + cache.getName(), cache.getExpireTime() / 1000, key.toString());
            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));
        }

        return CompletableFuture.runAsync(() -> caches.get(cache.getPlugin().getName() + ":" + cache.getName()).put(key, value));
    }

    public <T> CompletableFuture<T> getCachedObject(ObjectCache<?,T> cache, Object key) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.supplyAsync(() -> {
                Jedis jedis = pool.getResource();
                return (T) gson.fromJson(jedis.hget(cache.getPlugin().getName() + ":" + cache.getName(), gson.toJson(key)), Object.class);
            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));

        }

        return CompletableFuture.completedFuture((T) caches.get(cache.getPlugin().getName() + ":" + cache.getName()).getIfPresent(key));
    }

    public CompletableFuture<Void> removeCachedObject(ObjectCache<?,?> cache, Object key) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.runAsync(() -> {
                Jedis jedis = pool.getResource();
                jedis.hdel(cache.getPlugin().getName() + ":" + cache.getName(), gson.toJson(key));
                if (jedis.hlen(cache.getPlugin().getName() + ":" + cache.getName()) == 0) clearCache(cache);
            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));
        }

        return CompletableFuture.runAsync(() -> caches.get(cache.getPlugin().getName() + ":" + cache.getName()).invalidate(key));
    }

    public CompletableFuture<Void> clearCache(ObjectCache<?,?> cache) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.runAsync(() -> {
                Jedis jedis = pool.getResource();
                jedis.del(cache.getPlugin().getName() + ":" + cache.getName());
            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));
        }

        return CompletableFuture.runAsync(() -> caches.get(cache.getPlugin().getName() + ":" + cache.getName()).invalidateAll());
    }

    public CompletableFuture<Boolean> containsKey(ObjectCache<?,?> cache, Object key) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.supplyAsync(() -> {
                Jedis jedis = pool.getResource();
                return jedis.hexists(cache.getPlugin().getName() + ":" + cache.getName(), gson.toJson(key));
            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));
        }

        return CompletableFuture.completedFuture(caches.get(cache.getPlugin().getName() + ":" + cache.getName()).getIfPresent(key) != null);
    }

    public <K,V> CompletableFuture<Map<K, V>> getMap(ObjectCache<K,V> cache) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.supplyAsync(() -> {
                Jedis jedis = pool.getResource();
                Map<String, String> redisMap = jedis.hgetAll(cache.getPlugin().getName() + ":" + cache.getName());

                return redisMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> gson.fromJson(entry.getKey(), cache.getKeyType()),
                                entry -> gson.fromJson(entry.getValue(), cache.getValueType())
                        ));
            }, XG7Plugins.taskManager().getAsyncExecutors().get("cache"));
        }

        return CompletableFuture.completedFuture((Map<K, V>) caches.get(cache.getPlugin().getName() + ":" + cache.getName()).asMap());
    }







}
