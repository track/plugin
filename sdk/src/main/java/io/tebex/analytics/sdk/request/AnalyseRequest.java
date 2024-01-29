package io.tebex.analytics.sdk.request;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class for constructing and executing HTTP requests using the OkHttp library.
 */
public class AnalyseRequest {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("analyse-#%1$d").build());

    private final Request.Builder request;
    private final OkHttpClient client;

    /**
     * Constructs an AnalyseRequest instance with a specified endpoint and OkHttpClient.
     *
     * @param endpoint The URL to send the request to.
     * @param client   The OkHttpClient instance to be used for executing the request.
     */
    public AnalyseRequest(String endpoint, OkHttpClient client) {
        this.request = new Request.Builder().url(endpoint);
        this.client = client;
    }

    /**
     * Adds a header to the request.
     *
     * @param key   The header key.
     * @param value The header value.
     * @return The AnalyseRequest instance for chaining.
     */
    public AnalyseRequest withHeader(String key, String value) {
        request.addHeader(key, value);
        return this;
    }

    /**
     * Sets the request body.
     *
     * @param body The request body in JSON format.
     * @return The AnalyseRequest instance for chaining.
     */
    public AnalyseRequest withBody(String body) {
        request.post(RequestBody.create(body, MediaType.get("application/json; charset=utf-8")));
        return this;
    }

    /**
     * Adds the server token as a header to the request.
     *
     * @param token The server token.
     * @return The AnalyseRequest instance for chaining.
     */
    public AnalyseRequest withServerToken(String token) {
        return withHeader("X-SERVER-TOKEN", token);
    }

    /**
     * Builds the request into a Call object.
     *
     * @return The Call object representing the request.
     */
    public Call build() {
        return client.newCall(request.build());
    }

    /**
     * Executes the request synchronously.
     *
     * @return The Response object from the server.
     * @throws IOException If there is a problem executing the request.
     */
    public Response send() throws IOException {
        return build().execute();
    }

    /**
     * Executes the request asynchronously and returns a CompletableFuture.
     *
     * @return The CompletableFuture that will complete with the server Response.
     */
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
