package com.xg7plugins.modules.xg7menus.newMenuSystemAgain;

import com.xg7plugins.data.config.ConfigBoolean;

public @interface MenuConfigs {

    String id();
    MenuActions[] allowedActions();
    ConfigBoolean enabledPath() default @ConfigBoolean(
            path = "",
            configName = ""
    )

}
