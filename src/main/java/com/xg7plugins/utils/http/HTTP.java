package com.xg7plugins.utils.http;

import com.xg7plugins.utils.Pair;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class HTTP {

    public static HTTPRequest makeRequest(String url, HTTPMethod method, List<Pair<String,String>> headers, String body) throws IOException {
        return HTTPRequest.builder().
                url(url).
                method(method).
                headers(headers).
                body(body).
                build();
    }

    public static HTTPResponse get(String url) throws IOException {
        return makeRequest(url, HTTPMethod.GET, Collections.emptyList(), null).send();
    }

    public static HTTPResponse post(String url, String body) throws IOException {
        return makeRequest(url, HTTPMethod.POST, Collections.emptyList(), body).send();
    }

    public static HTTPResponse put(String url, String body) throws IOException {
        return makeRequest(url, HTTPMethod.PUT, Collections.emptyList(), body).send();
    }

    public static HTTPResponse delete(String url) throws IOException {
        return makeRequest(url, HTTPMethod.DELETE, Collections.emptyList(), null).send();
    }

    public static HTTPResponse get(String url, List<Pair<String,String>> headers) throws IOException {
        return makeRequest(url, HTTPMethod.GET, headers, null).send();
    }

    public static HTTPResponse post(String url, String body, List<Pair<String,String>> headers) throws IOException {
        return makeRequest(url, HTTPMethod.POST, headers, body).send();
    }

    public static HTTPResponse put(String url, String body, List<Pair<String,String>> headers) throws IOException {
        return makeRequest(url, HTTPMethod.PUT, headers, body).send();
    }

    public static HTTPResponse delete(String url, List<Pair<String,String>> headers) throws IOException {
        return makeRequest(url, HTTPMethod.DELETE, headers, null).send();
    }






}
