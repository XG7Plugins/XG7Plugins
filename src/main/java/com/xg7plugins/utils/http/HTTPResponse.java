package com.xg7plugins.utils.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HTTPResponse {

    private final String content;
    private final int statusCode;
    private final String statusMessage;

    public JsonObject getJson() {
        return new JsonParser().parse(content).getAsJsonObject();
    }

    public <T> T getObject(Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(content, clazz);
    }


}
