package com.xg7plugins.utils.http;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.Pair;
import lombok.AllArgsConstructor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class HTTPRequest {

    private final String url;

    private final HTTPMethod method;

    private final List<Pair<String,String>> headers;

    private final String body;

    public HTTPResponse send() throws IOException {
        URL url = new URL(this.url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method.name());

        conn.setConnectTimeout(Config.mainConfigOf(XG7Plugins.getInstance()).getTime("http-request-timeout").orElse(5000L).intValue());

        for (Pair<String,String> header : headers) conn.setRequestProperty(header.getFirst(), header.getSecond());

        if (body != null && !body.isEmpty()) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        conn.disconnect();
        return new HTTPResponse(sb.toString(), conn.getResponseCode(), conn.getResponseMessage());
    }
    public InputStream getInputStream() throws IOException {
        URL url = new URL(this.url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method.name());

        conn.setConnectTimeout(Config.mainConfigOf(XG7Plugins.getInstance()).getTime("http-request-timeout").orElse(5000L).intValue());

        for (Pair<String,String> header : headers) conn.setRequestProperty(header.getFirst(), header.getSecond());

        if (body != null && !body.isEmpty()) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }
        return conn.getInputStream();
    }

    public static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private String url;
        private HTTPMethod method;
        private List<Pair<String,String>> headers = new ArrayList<>();
        private String body = "";

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(HTTPMethod method) {
            this.method = method;
            return this;
        }

        public Builder headers(List<Pair<String,String>> headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HTTPRequest build() {
            if (url == null || method == null) {
                throw new IllegalArgumentException("URL and method must be set");
            }
            return new HTTPRequest(url, method, headers, body);
        }
    }


}
