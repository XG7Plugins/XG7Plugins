package com.xg7plugins.boot;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
public class EnvironmentConfig {
    private String customPrefix;
    private String prefix;
    private List<String> enabledWorlds;
}
