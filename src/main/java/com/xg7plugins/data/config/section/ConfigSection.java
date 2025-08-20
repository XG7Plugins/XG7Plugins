package com.xg7plugins.data.config.section;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.time.Time;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for managing config sections.
 * Provides functionality to access, save and modify configuration values with type safety.
 * <p>
 * Usage examples can be found in {@link com.xg7plugins.data.config.core.MainConfigSection}
 * which extends the base config functionality.
 * The class automatically maps configuration values to fields using reflection.
 */
public abstract class ConfigSection {

    @Getter
    private final Config config;
    private final String mainSection;

    /**
     * Constructor that validates the {@link ConfigFile} annotation and initializes the config.
     */
    public ConfigSection() {
        if (!this.getClass().isAnnotationPresent(ConfigFile.class)) {
            throw new IllegalStateException("ConfigSection class must be annotated with @ConfigFile");
        }
        ConfigFile configFile = getConfigFile();

        Plugin plugin = XG7PluginsAPI.getXG7Plugin(configFile.plugin());

        this.config = Config.of(configFile.configName(), plugin);
        this.mainSection = configFile.path() + ".";

        try {
            setFieldValues();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ConfigFile getConfigFile() {
        return this.getClass().getAnnotation(ConfigFile.class);
    }

    /**
     * Reloads the config values from file and updates all fields
     */
    public void reload() {
        config.reload();
        try {
            setFieldValues();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets a config value and saves to file
     * Used by forms like LangForm to persist changes
     */
    public void setValue(String key, Object value) {
        config.set(mainSection + key, value);
        save();
    }

    /**
     * Saves current values to config file and updates fields
     */
    public void save() {
        config.save();
        try {
            setFieldValues();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Maps config values to class fields using reflection
     */
    public void setFieldValues() throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            setFieldValue(field);
        }
    }

    /**
     * Sets a field's value based on its type from the config
     * Supports primitive types, Time and custom objects
     */
    private void setFieldValue(Field field) throws IllegalAccessException {

        Class<?> type = field.getType();
        String configPath = translate(field.getName());

        Object defaultValue = null;

        if (field.isAnnotationPresent(ConfigField.class)) {
            ConfigField configField = field.getAnnotation(ConfigField.class);

            configPath = configField.name().isEmpty() ? configPath : configField.defaultValue();

            Parser parser = Parser.getParserOf(type);

            defaultValue = configField.defaultValue().isEmpty() || parser == null ? DefaultValues.ofType(type).getDefaultValue() : parser.convert(configField.defaultValue());

        }

        switch (field.getType().getSimpleName()) {
            case "String":
                field.set(this, config.get(mainSection + configPath, String.class).orElse((String) defaultValue));
                return;
            case "Integer":
            case "int":
                field.set(this, config.get(mainSection + configPath, Integer.class).orElse((Integer) defaultValue));
                return;
            case "Boolean":
            case "boolean":
                field.set(this, config.get(mainSection + configPath, Boolean.class).orElse((Boolean) defaultValue));
                return;
            case "Double":
            case "double":
                field.set(this, config.get(mainSection + configPath, Double.class).orElse((Double) defaultValue));
                return;
            case "Float":
            case "float":
                field.set(this, config.get(mainSection + configPath, Float.class).orElse((Float) defaultValue));
                return;
            case "Long":
            case "long":
                field.set(this, config.get(mainSection + configPath, Long.class).orElse((Long) defaultValue));
                return;
            case "Time":
                field.set(this, config.getTime(mainSection + configPath).orElse((Time) defaultValue));
            case "List":
                setFieldListValue(field);
                return;
            default:
                field.set(this, config.get(mainSection + configPath, field.getType()).orElse(null));
        }

    }

    /**
     * Sets a List field's value from config
     * Used for arrays like enabledWorlds in EnvironmentConfig
     */
    private void setFieldListValue(Field field) throws IllegalAccessException {

        Type genericType = field.getGenericType();

        String configName = translate(field.getName());

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type[] typeArguments = pt.getActualTypeArguments();

            Class<?> listType = (Class<?>) typeArguments[0];

            String path = mainSection + configName;
            List<?> value = config.getList(path, listType).orElse(new ArrayList<>());
            field.set(this, value);
        }
    }

    /**
     * Converts field names from camelCase to kebab-case for config paths
     */
    private String translate(String input) {
        if (!input.contains("_")) return input.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
        String[] parts = input.split("_", 2);
        String section = translate(parts[0]);
        String part = translate(parts[1]);
        return section + "." + part;

    }

}