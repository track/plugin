package net.analyse.sdk.request;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalyseRequest {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("analyse-#%1$d").build());

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

    public Call build() {
        return client.newCall(request.build());
    }

    public Response send() throws IOException {
        return build().execute();
    }

    public CompletableFuture<Response> sendAsync() {
        CompletableFuture<Response> future = new CompletableFuture<>();

        EXECUTOR.submit(() -> this.build().enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                future.complete(response);
                response.close();
            }
        }));

        return future;
    }
}
