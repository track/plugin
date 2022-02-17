package net.analyse.plugin.request;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PluginAPIRequest {

    private static final String BASE_URL = "http://app.analyse.net.test/api/v1/";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Request.Builder request;

    public PluginAPIRequest(final @NotNull String url) {
        this.request = new Request.Builder().url(BASE_URL + url);
    }

    public PluginAPIRequest withPayload(final @NotNull String payload) {
        request.post(RequestBody.create(payload, JSON));

        return this;
    }

    public PluginAPIRequest withServerToken(final @NotNull String token) {
        request.header("X-SERVER-TOKEN", token);

        return this;
    }

    public Request.Builder getRequest() {
        return request;
    }

    public Response send() {
        try {
            return client.newCall(request.build()).execute();
        } catch (IOException e) {
            // TODO: Handle this.
            e.printStackTrace();
            return null;
        }
    }
}
