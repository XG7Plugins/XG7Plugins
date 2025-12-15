package com.xg7plugins.config.file;

import java.util.Map;

public class ConfigVersionMigration {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> migrate(Map<String, Object> oldConfig, Map<String, Object> newConfig) {

        for (String key : newConfig.keySet()) {

            if (key.equals("config-version")) continue;

            Object newVal = newConfig.get(key);

            if (newVal instanceof Map
                    && oldConfig.containsKey(key)
                    && oldConfig.get(key) instanceof Map) {

                newConfig.put(
                        key,
                        migrate(
                                (Map<String, Object>) oldConfig.get(key),
                                (Map<String, Object>) newVal
                        )
                );
                continue;
            }

            if (oldConfig.containsKey(key)) {
                newConfig.put(key, oldConfig.get(key));
            }
        }

        return newConfig;
    }





}
