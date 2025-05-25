package com.xg7plugins.utils.http;

import com.xg7plugins.utils.Pair;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Utility class providing convenient methods for making HTTP requests.
 * Supports basic HTTP methods (GET, POST, PUT, DELETE) with optional headers and body.
 */
public class HTTP {

    /**
     * Creates an HTTP request with the specified parameters.
     *
     * @param url     The URL to send the request to
     * @param method  The HTTP method to use (GET, POST, PUT, DELETE)
     * @param headers List of header key-value pairs to include in the request
     * @param body    The request body (for POST and PUT requests)
     * @return A configured HTTPRequest object ready to be sent
     * @throws IOException If there's an error creating the request
     */
    public static HTTPRequest makeRequest(String url, HTTPMethod method, List<Pair<String,String>> headers, String body) throws IOException {
        return HTTPRequest.builder().
                url(url).
                method(method).
                headers(headers).
                body(body).
                build();
    }

    /**
     * Performs a GET request to the specified URL without headers.
     *
     * @param url The URL to send the GET request to
     * @return The HTTP response from the server
     * @throws IOException If there's an error during the request
     */
    public static HTTPResponse get(String url) throws IOException {
        return makeRequest(url, HTTPMethod.GET, Collections.emptyList(), null).send();
    }

    /**
     * Performs a POST request to the specified URL with a body but without headers.
     *
     * @param url  The URL to send the POST request to
     * @param body The request body to send
     * @return The HTTP response from the server
     * @throws IOException If there's an error during the request
     */
    public static HTTPResponse post(String url, String body) throws IOException {
        return makeRequest(url, HTTPMethod.POST, Collections.emptyList(), body).send();
    }

    /**
     * Performs a PUT request to the specified URL with a body but without headers.
     *
     * @param url  The URL to send the PUT request to
     * @param body The request body to send
     * @return The HTTP response from the server
     * @throws IOException If there's an error during the request
     */
    public static HTTPResponse put(String url, String body) throws IOException {
        return makeRequest(url, HTTPMethod.PUT, Collections.emptyList(), body).send();
    }

    /**
     * Performs a DELETE request to the specified URL without headers.
     *
     * @param url The URL to send the DELETE request to
     * @return The HTTP response from the server
     * @throws IOException If there's an error during the request
     */
    public static HTTPResponse delete(String url) throws IOException {
        return makeRequest(url, HTTPMethod.DELETE, Collections.emptyList(), null).send();
    }

    /**
     * Performs a GET request to the specified URL with custom headers.
     *
     * @param url     The URL to send the GET request to
     * @param headers List of header key-value pairs to include in the request
     * @return The HTTP response from the server
     * @throws IOException If there's an error during the request
     */
    public static HTTPResponse get(String url, List<Pair<String,String>> headers) throws IOException {
        return makeRequest(url, HTTPMethod.GET, headers, null).send();
    }

    /**
     * Performs a POST request to the specified URL with a body and custom headers.
     *
     * @param url     The URL to send the POST request to
     * @param body    The request body to send
     * @param headers List of header key-value pairs to include in the request
     * @return The HTTP response from the server
     * @throws IOException If there's an error during the request
     */
    public static HTTPResponse post(String url, String body, List<Pair<String,String>> headers) throws IOException {
        return makeRequest(url, HTTPMethod.POST, headers, body).send();
    }

    /**
     * Performs a PUT request to the specified URL with a body and custom headers.
     *
     * @param url     The URL to send the PUT request to
     * @param body    The request body to send
     * @param headers List of header key-value pairs to include in the request
     * @return The HTTP response from the server
     * @throws IOException If there's an error during the request
     */
    public static HTTPResponse put(String url, String body, List<Pair<String,String>> headers) throws IOException {
        return makeRequest(url, HTTPMethod.PUT, headers, body).send();
    }

    /**
     * Performs a DELETE request to the specified URL with custom headers.
     *
     * @param url     The URL to send the DELETE request to
     * @param headers List of header key-value pairs to include in the request
     * @return The HTTP response from the server
     * @throws IOException If there's an error during the request
     */
    public static HTTPResponse delete(String url, List<Pair<String,String>> headers) throws IOException {
        return makeRequest(url, HTTPMethod.DELETE, headers, null).send();
    }






}
