package com.xg7plugins.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;

import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Manages caching operations using either Redis for distributed caching or Caffeine for local caching.
 * This class handles cache registration, object storage, retrieval, and cache invalidation.
 * It supports both local memory caching and distributed Redis caching based on configuration.
 */
public class CacheManager {

    /**
     * Redis connection pool for distributed caching
     */
    private JedisPool pool;

    /**
     * JSON serializer/deserializer for object conversion
     */
    private final Gson gson = new Gson();

    /**
     * Flag indicating whether cached items should expire
     */
    private boolean cacheExpires = true;

    /**
     * Local cache storage using Caffeine cache implementation
     */
    private final HashMap<String, Cache<@NotNull Object, Object>> caches;

    public CacheManager(XG7Plugins plugin) {

        ConfigSection config = ConfigFile.mainConfigOf(plugin).section("redis-cache");

        this.caches = new HashMap<>();

        if (!config.get("enabled", false)) return;

        this.cacheExpires = config.get("cache-expires", true);

        int minIdle = config.get("min-idle-connections", 4);

        try {
            pool = config.get("user-auth-enabled", false) ? new JedisPool(config.get("host"), config.get("port", 6379), config.get("username"), config.get("password")) : new JedisPool(config.get("host", "localhost"), config.get("port", 6379));

            pool.setMinIdle(minIdle);
            pool.setMaxIdle(config.get("max-idle-connections", 8));
            pool.setMaxTotal(config.get("max-connections", 8));
            pool.setMaxWaitMillis(config.get("max-wait-time", 2000L));

            Jedis jedis = pool.getResource();
            if (config.get("user-auth-enabled", false)) jedis.auth(config.get("password", ""));
            jedis.close();
        } catch (Exception e) {
            plugin.getDebug().severe("Failed to connect to Redis: " + e.getMessage());
            plugin.getDebug().severe("Using local cache instead.");
        }


    }

    /**
     * Registers a new cache instance either locally or in Redis
     *
     * @param cache The cache configuration object to register
     */
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

    /**
     * Closes the Redis connection pool and performs cleanup
     */
    public void shutdown() {
        if (pool != null) {
            pool.close();
        }
    }

    /**
     * Stores an object in the cache asynchronously
     *
     * @param cache The cache configuration to use
     * @param key   The key to store the value under
     * @param value The value to cache
     * @return A CompletableFuture that completes when the operation is done
     */
    public CompletableFuture<Void> cacheObject(ObjectCache<?,?> cache, Object key, Object value) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.runAsync(() -> {
                try (Jedis jedis = pool.getResource()) {
                    jedis.hset(cache.getPlugin() + ":" + cache.getName(), gson.toJson(key), gson.toJson(value));
                    if (cacheExpires) jedis.hexpire(cache.getPlugin().getName() + ":" + cache.getName(), cache.getExpireTime() / 1000, key.toString());
                }

            }, XG7Plugins.getAPI().taskManager().getExecutor("cache"));
        }
        caches.get(cache.getPlugin().getName() + ":" + cache.getName()).put(key, value);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Retrieves a cached object asynchronously
     *
     * @param cache The cache configuration to use
     * @param key   The key to look up
     * @param <T>   The type of value to return
     * @return A CompletableFuture containing the cached value, or null if not found
     */
    public <T> CompletableFuture<T> getCachedObject(ObjectCache<?,T> cache, Object key) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.supplyAsync(() -> {
                try (Jedis jedis = pool.getResource()) {
                    return (T) gson.fromJson(jedis.hget(cache.getPlugin().getName() + ":" + cache.getName(), gson.toJson(key)), Object.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }, XG7Plugins.getAPI().taskManager().getExecutor("cache"));

        }

        return CompletableFuture.completedFuture((T) caches.get(cache.getPlugin().getName() + ":" + cache.getName()).getIfPresent(key));
    }

    /**
     * Removes an object from the cache asynchronously
     *
     * @param cache The cache configuration to use
     * @param key   The key to remove
     * @return A CompletableFuture that completes when the operation is done
     */
    public CompletableFuture<Void> removeCachedObject(ObjectCache<?,?> cache, Object key) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.runAsync(() -> {
                try (Jedis jedis = pool.getResource()) {
                    jedis.hdel(cache.getPlugin().getName() + ":" + cache.getName(), gson.toJson(key));
                    if (jedis.hlen(cache.getPlugin().getName() + ":" + cache.getName()) == 0) clearCache(cache);
                }
            }, XG7Plugins.getAPI().taskManager().getExecutor("cache"));
        }

        caches.get(cache.getPlugin().getName() + ":" + cache.getName()).invalidate(key);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Clears all entries from a specific cache asynchronously
     *
     * @param cache The cache configuration to clear
     * @return A CompletableFuture that completes when the operation is done
     */
    public CompletableFuture<Void> clearCache(ObjectCache<?,?> cache) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.runAsync(() -> {
                try (Jedis jedis = pool.getResource()) {
                    jedis.del(cache.getPlugin().getName() + ":" + cache.getName());
                }
            }, XG7Plugins.getAPI().taskManager().getExecutor("cache"));
        }

        caches.get(cache.getPlugin().getName() + ":" + cache.getName()).invalidateAll();
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Checks if a key exists in the cache asynchronously
     *
     * @param cache The cache configuration to check
     * @param key   The key to look for
     * @return A CompletableFuture containing true if the key exists, false otherwise
     */
    public CompletableFuture<Boolean> containsKey(ObjectCache<?,?> cache, Object key) {
        if (pool != null && !cache.isUseLocalCache()) {
            return CompletableFuture.supplyAsync(() -> {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.hexists(cache.getPlugin().getName() + ":" + cache.getName(), gson.toJson(key));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }, XG7Plugins.getAPI().taskManager().getExecutor("cache"));
        }

        return CompletableFuture.completedFuture(caches.get(cache.getPlugin().getName() + ":" + cache.getName()).getIfPresent(key) != null);
    }

    /**
     * Retrieves all entries from a cache as a Map asynchronously
     *
     * @param cache The cache configuration to get entries from
     * @param <K>   The key type
     * @param <V>   The value type
     * @return A CompletableFuture containing a Map of all cache entries
     */
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
            }, XG7Plugins.getAPI().taskManager().getExecutor("cache"));
        }

        return CompletableFuture.completedFuture((Map<K, V>) caches.get(cache.getPlugin().getName() + ":" + cache.getName()).asMap());
    }







}
