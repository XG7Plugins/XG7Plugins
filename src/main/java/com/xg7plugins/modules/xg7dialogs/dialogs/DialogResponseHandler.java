package com.xg7plugins.modules.xg7dialogs.dialogs;

import java.util.Map;

@FunctionalInterface
public interface DialogResponseHandler {

    void onResponse(String channel, Map<String, Object> payload);

}
