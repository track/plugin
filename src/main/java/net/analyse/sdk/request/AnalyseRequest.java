package net.analyse.sdk.request;

import okhttp3.*;

import java.io.IOException;

public class AnalyseRequest {
    private final Request.Builder request;
    private final OkHttpClient client;

    public AnalyseRequest(String endpoint, OkHttpClient client) {
        this.request = new Request.Builder().url(endpoint);
        this.client = client;
    }

    public AnalyseRequest withHeader(String key, String value) {
        request.addHeader(key, value);
        return this;
    }

    public AnalyseRequest withBody(String body) {
        request.post(RequestBody.create(body, MediaType.get("application/json; charset=utf-8")));
        return this;
    }

    public AnalyseRequest withServerToken(String token) {
        return withHeader("X-SERVER-TOKEN", token);
    }

    public Response send() throws IOException {
        return client.newCall(request.build()).execute();
    }
}
