package com.xg7plugins.data.config.section;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.time.Time;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class ConfigSection {

    @Getter
    private final Config config;
    private final String mainSection;

    public ConfigSection() {
        if (!this.getClass().isAnnotationPresent(ConfigFile.class)) {
            throw new IllegalStateException("ConfigSection class must be annotated with @ConfigFile");
        }
        ConfigFile configFile = getConfigFile();

        Plugin plugin = XG7PluginsAPI.getXG7Plugin(configFile.plugin());

        this.config = Config.of(configFile.configName(),  plugin);
        this.mainSection = configFile.path();

        try {
            setFieldValues();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ConfigFile getConfigFile() {
        return this.getClass().getAnnotation(ConfigFile.class);
    };

    public void reload() {
        config.reload();
        try {
            setFieldValues();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(String key, Object value) {
        config.set(mainSection + key, value);
        save();
    }

    public void save() {
        config.save();
        try {
            setFieldValues();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFieldValues() throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
             field.setAccessible(true);
             setFieldValue(field);
        }
    }

    private void setFieldValue(Field field) throws IllegalAccessException {

        Class<?> type = field.getType();
        String configPath = camelToKebab(field.getName());

        switch (field.getType().getSimpleName()) {
            case "String":
                field.set(this, config.get(mainSection + configPath, String.class).orElse(null));
                return;
            case "Integer":
            case "int":
                field.set(this, config.get(mainSection + configPath, Integer.class).orElse(0));
                return;
            case "Boolean":
            case "boolean":
                field.set(this, config.get(mainSection + configPath, Boolean.class).orElse(false));
                return;
            case "Double":
            case "double":
                field.set(this, config.get(mainSection + configPath, Double.class).orElse(0.0));
                return;
            case "Float":
            case "float":
                field.set(this, config.get(mainSection + configPath, Float.class).orElse(0f));
                return;
            case "Long":
            case "long":
                field.set(this, config.get(mainSection + configPath, Long.class).orElse(0L));
                return;
            case "Time":
                field.set(this, config.getTime(mainSection + configPath).orElse(Time.of(0)));
            case "List":
                setFieldListValue(field);
                return;
            default:
                field.set(this, config.get(mainSection + configPath, field.getType()).orElse(null));
        }

    }

    private void setFieldListValue(Field field) throws IllegalAccessException {

        Type genericType = field.getGenericType();

        String configName = camelToKebab(field.getName());

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type[] typeArguments = pt.getActualTypeArguments();

            Class<?> listType = (Class<?>) typeArguments[0];

            String path = mainSection + configName;
            List<?> value = config.getList(path, listType).orElse(new ArrayList<>());
            field.set(this, value);
        }
    }

    private String camelToKebab(String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }

}
