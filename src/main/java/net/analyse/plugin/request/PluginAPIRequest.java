package net.analyse.plugin.request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PluginAPIRequest {
    private final String BASE_URL = "http://app.analyse.net.test/api/v1/";
    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpRequest.Builder request;

    public PluginAPIRequest(String url) {
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
        } catch (IOException e) {
            // TODO: Handle this.
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO: Handle this.
            e.printStackTrace();
        }

        return null;
    }
}
