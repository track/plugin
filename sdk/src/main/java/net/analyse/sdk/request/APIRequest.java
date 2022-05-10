package net.analyse.sdk.request;

import net.analyse.sdk.AnalyseCore;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class APIRequest {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Request.Builder request;

    public APIRequest(final @NotNull String url, final @NotNull OkHttpClient client, final @NotNull String apiHeader) {
        this.request = new Request.Builder().url(url).header("User-Agent", apiHeader);
        this.client = client;
    }

    public APIRequest withPayload(final @NotNull String payload) {
        request.post(RequestBody.create(payload, JSON));

        return this;
    }

    public APIRequest withServerToken(final @NotNull String token) {
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
