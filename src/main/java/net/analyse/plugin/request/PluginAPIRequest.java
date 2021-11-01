package net.analyse.plugin.request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PluginAPIRequest {

    private final String baseURL = "http://127.0.0.1:8000/api/v1/";
    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpRequest.Builder request;

    public PluginAPIRequest(String url) {
        this.request = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + url));
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
