package net.analyse.sdk.request;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A request to the Analyse API.
 */
public class AnalyseRequest {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("analyse-#%1$d").build());

    private final Request.Builder request;
    private final OkHttpClient client;

    /**
     * Create a new request.
     * @param endpoint The endpoint to send the request to.
     * @param client The client to use.
     */
    public AnalyseRequest(String endpoint, OkHttpClient client) {
        this.request = new Request.Builder().url(endpoint);
        this.client = client;
    }

    /**
     * Add a header to the request.
     * @param key The header key.
     * @param value The header value.
     * @return The request.
     */
    public AnalyseRequest withHeader(String key, String value) {
        request.addHeader(key, value);
        return this;
    }

    /**
     * Add a body to the request.
     * @param body The body.
     * @return The request.
     */
    public AnalyseRequest withBody(String body) {
        request.post(RequestBody.create(body, MediaType.get("application/json; charset=utf-8")));
        return this;
    }

    /**
     * Add a server token to the request.
     * @param token The server token.
     * @return The request.
     */
    public AnalyseRequest withServerToken(String token) {
        return withHeader("X-SERVER-TOKEN", token);
    }

    /**
     * Build the request.
     * @return The request.
     */
    public Call build() {
        return client.newCall(request.build());
    }

    /**
     * Send the request synchronously.
     * @return The response.
     * @throws IOException If an error occurs while sending the request.
     */
    public Response send() throws IOException {
        return build().execute();
    }

    /**
     * Send the request asynchronously.
     * @return A future that will be completed with the response.
     */
    public CompletableFuture<Response> sendAsync() {
        CompletableFuture<Response> future = new CompletableFuture<>();

        EXECUTOR.execute(() -> {
            try (Response response = send()) {
                future.complete(response);
            } catch (IOException e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }
}
