package com.xg7plugins.utils.http;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.time.Time;
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
        HttpURLConnection conn = request();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (InputStream in = conn.getInputStream()) {
            byte[] data = new byte[8192];
            int n;
            while ((n = in.read(data)) != -1) {
                buffer.write(data, 0, n);
            }
        }

        int code = conn.getResponseCode();
        String message = conn.getResponseMessage();
        conn.disconnect();

        byte[] bytes = buffer.toByteArray();

        String content = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);

        return new HTTPResponse(
                content,
                bytes,
                code,
                message
        );
    }

    /**
     * Creates and configures an HTTP connection with specified parameters.
     * Sets default headers including User-Agent, Accept, Language, and Cache settings.
     * Configures timeout from plugin config and applies custom headers if provided.
     * Handles request body if present and logs debug information about the request.
     *
     * @return Configured HttpURLConnection ready for execution
     * @throws IOException if connection setup or request execution fails
     */
    private HttpURLConnection request() throws IOException {

        Debug debug = Debug.of(XG7Plugins.getInstance());

        debug.info("http-requests", "Making request to: " + this.url);

        URL url = new URL(this.url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method.name());

        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Pragma", "no-cache");

        conn.setConnectTimeout((int) ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("http-requests-timeout", Time.of(5)).toMilliseconds());

        if (headers != null) for (Pair<String,String> header : headers) conn.setRequestProperty(header.getFirst(), header.getSecond());

        if (body != null && !body.isEmpty()) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        return conn;
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
