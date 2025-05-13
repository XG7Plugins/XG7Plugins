package com.xg7plugins.cache;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    public ObjectCache(Plugin plugin, long expireTime, boolean expiresAfterWrite, String name, boolean useLocalCache, Class<K> keyType, Class<V> valueType) {
        this.plugin = plugin;
        this.expireTime = expireTime;
        this.expiresAfterWrite = expiresAfterWrite;
        this.name = name;
        this.cacheManager = XG7PluginsAPI.cacheManager();

        this.useLocalCache = useLocalCache;
        this.keyType = keyType;
        this.valueType = valueType;

        cacheManager.registerCache(this);

    }

    public CompletableFuture<Void> put(K key, V value) {
        return cacheManager.cacheObject(this, key, value);
    }

    public CompletableFuture<V> get(K key) {
        return cacheManager.getCachedObject(this, key);
    }

    public CompletableFuture<Void> remove(K key) {
        return cacheManager.removeCachedObject(this, key);
    }

    public CompletableFuture<Void> clear() {
        return cacheManager.clearCache(this);
    }

    public CompletableFuture<Boolean> containsKey(K key) {
        return cacheManager.containsKey(this, key);
    }

    public CompletableFuture<Map<K, V>> asMap() {
        return cacheManager.getMap(this);
    }

}
