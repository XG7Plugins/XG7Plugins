package com.xg7plugins.utils.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Represents an HTTP response with content, status code, and status message.
 * Provides methods to parse the response content as JSON or convert it to Java objects.
 */
@AllArgsConstructor
@Getter
public class HTTPResponse {

    /**
     * The raw content of the HTTP response
     */
    private final String content;
    private final byte[] contentBytes;

    /**
     * The HTTP status code of the response
     */
    private final int statusCode;
    /**
     * The HTTP status message associated with the status code
     */
    private final String statusMessage;

    /**
     * Converts the response content to a JsonObject.
     *
     * @return JsonObject representation of the response content
     */
    public JsonObject getJson() {
        return new JsonParser().parse(content).getAsJsonObject();
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(contentBytes);
    }

    /**
     * Converts the response content to a specified Java object type.
     *
     * @param clazz The class type to convert the content to
     * @param <T>   The type parameter for the target class
     * @return An instance of the specified class containing the response data
     */
    public <T> T getObject(Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(content, clazz);
    }


}
