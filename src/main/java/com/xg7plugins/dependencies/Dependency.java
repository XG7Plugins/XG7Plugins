package com.xg7plugins.dependencies;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class Dependency {

    public boolean isLoaded;
    private String name;
    private String downloadLink;

}
