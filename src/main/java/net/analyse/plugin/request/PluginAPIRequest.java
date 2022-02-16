package net.analyse.plugin.request;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PluginAPIRequest {

    private static final String BASE_URL = "https://app.analyse.net/api/v1/";
    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpRequest.Builder request;

    public PluginAPIRequest(final @NotNull String url) {
        this.request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create(BASE_URL + url));
    }

    public HttpRequest.Builder getRequest() {
        return request;
    }

    public HttpResponse<String> send() {
        try {
            return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            // TODO: Handle this.
            e.printStackTrace();
        }

        return null;
    }
}
