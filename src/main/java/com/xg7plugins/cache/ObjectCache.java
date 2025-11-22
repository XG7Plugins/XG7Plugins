package com.xg7plugins.cache;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Generic cache implementation for storing key-value pairs with optional local caching support.
 * Provides asynchronous operations for storing, retrieving, and managing cached objects.
 *
 * @param <K> The type of keys maintained by this cache
 * @param <V> The type of mapped values
 */
@Getter
public class ObjectCache<K,V> {

    private final Plugin plugin;
    private final long expireTime;
    private final boolean expiresAfterWrite;
    private final String name;
    private final CacheManager cacheManager;
    private final boolean useLocalCache;
    private final Class<K> keyType;
    private final Class<V> valueType;

    /**
     * Creates a new ObjectCache instance with specified configuration.
     *
     * @param plugin            The plugin that owns this cache
     * @param expireTime        Time in milliseconds after which cache entries expire
     * @param expiresAfterWrite If true, entries expire after write; if false, after access
     * @param name              Unique identifier for this cache
     * @param useLocalCache     Whether to use local cache in addition to distributed cache
     * @param keyType           Class type of the cache keys
     * @param valueType         Class type of the cached values
     */
    public ObjectCache(Plugin plugin, long expireTime, boolean expiresAfterWrite, String name, boolean useLocalCache, Class<K> keyType, Class<V> valueType) {
        this.plugin = plugin;
        this.expireTime = expireTime;
        this.expiresAfterWrite = expiresAfterWrite;
        this.name = name;
        this.cacheManager = XG7Plugins.getAPI().cacheManager();

        this.useLocalCache = useLocalCache;
        this.keyType = keyType;
        this.valueType = valueType;

        cacheManager.registerCache(this);

    }

    /**
     * Asynchronously stores a value with the specified key in the cache.
     *
     * @param key   The key under which to store the value
     * @param value The value to be cached
     * @return A CompletableFuture that completes when the operation is done
     */
    public CompletableFuture<Void> put(K key, V value) {
        return cacheManager.cacheObject(this, key, value);
    }

    /**
     * Asynchronously retrieves the value associated with the specified key.
     *
     * @param key The key whose associated value is to be returned
     * @return A CompletableFuture containing the value, or null if not found
     */
    public CompletableFuture<V> get(K key) {
        return cacheManager.getCachedObject(this, key);
    }

    /**
     * Asynchronously removes the entry for the specified key if present.
     *
     * @param key The key whose mapping is to be removed
     * @return A CompletableFuture that completes when the operation is done
     */
    public CompletableFuture<Void> remove(K key) {
        return cacheManager.removeCachedObject(this, key);
    }

    /**
     * Asynchronously removes all entries from this cache.
     *
     * @return A CompletableFuture that completes when the operation is done
     */
    public CompletableFuture<Void> clear() {
        return cacheManager.clearCache(this);
    }

    /**
     * Asynchronously checks if the cache contains a mapping for the specified key.
     *
     * @param key The key whose presence is to be tested
     * @return A CompletableFuture containing true if the key exists, false otherwise
     */
    public CompletableFuture<Boolean> containsKey(K key) {
        return cacheManager.containsKey(this, key);
    }

    /**
     * Asynchronously returns a Map view of all cache entries.
     *
     * @return A CompletableFuture containing a Map of all cache entries
     */
    public CompletableFuture<Map<K, V>> asMap() {
        return cacheManager.getMap(this);
    }

}
