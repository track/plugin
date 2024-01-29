package io.tebex.analytics.sdk;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.tebex.analytics.sdk.exception.ServerNotFoundException;
import io.tebex.analytics.sdk.exception.ServerNotSetupException;
import io.tebex.analytics.sdk.obj.AnalysePlayer;
import io.tebex.analytics.sdk.platform.Platform;
import io.tebex.analytics.sdk.platform.PlatformType;
import io.tebex.analytics.sdk.request.AnalyseRequest;
import io.tebex.analytics.sdk.request.exception.RateLimitException;
import io.tebex.analytics.sdk.request.response.AnalyseLeaderboard;
import io.tebex.analytics.sdk.request.response.PlayerProfile;
import io.tebex.analytics.sdk.request.response.PluginInformation;
import io.tebex.analytics.sdk.request.response.ServerInformation;
import io.tebex.analytics.sdk.util.StringUtil;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * The main SDK class for interacting with the Analytics API.
 */
public class SDK {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    private final OkHttpClient HTTP_CLIENT;

    private final int API_VERSION = 1;
    private final String API_URL = String.format("https://analytics.tebex.io/api/v%d", API_VERSION);

    private final Platform platform;
    private String serverToken;

    /**
     * Constructs a new SDK instance with the specified platform and server token.
     *
     * @param platform    The platform on which the SDK is running.
     * @param serverToken The server token for authentication.
     */
    public SDK(Platform platform, String serverToken) {
        this.platform = platform;
        this.serverToken = serverToken;

        TrustManager TRUST_ALL_CERTS = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }
            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient()
                .newBuilder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                .retryOnConnectionFailure(true);
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[] { TRUST_ALL_CERTS }, new java.security.SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) TRUST_ALL_CERTS);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }

        this.HTTP_CLIENT = builder.build();
    }

    /**
     * Retrieves the latest plugin information.
     *
     * @param platformType The platform type for which to retrieve the plugin information.
     * @return A CompletableFuture that contains the PluginInformation object.
     */
    public CompletableFuture<PluginInformation> getPluginVersion(PlatformType platformType) {
        return request("/plugin").sendAsync().thenApply(response -> {
            if(response.code() == 404) {
                throw new CompletionException(new ServerNotFoundException());
            } else if(response.code() == 429) {
                throw new CompletionException(new RateLimitException("You are being rate limited."));
            } else if(response.code() != 200) {
                throw new CompletionException(new IOException("Unexpected status code (" + response.code() + ")"));
            }

            try {
                JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                JsonObject versionData = jsonObject.get("version").getAsJsonObject();
                JsonObject assetData = jsonObject.get("assets").getAsJsonObject();

                return new PluginInformation(
                        versionData.get("name").getAsString(),
                        versionData.get("incremental").getAsInt(),
                        assetData.get(platformType.name().toLowerCase()).getAsString()
                );
            } catch (IOException e) {
                throw new CompletionException(new IOException("Unexpected response"));
            }
        });
    }

    /**
     * Retrieves information about the server.
     *
     * @return A CompletableFuture that contains the ServerInformation object.
     */
    public CompletableFuture<ServerInformation> getServerInformation() {
        if (getServerToken() == null) {
            CompletableFuture<ServerInformation> future = new CompletableFuture<>();
            future.completeExceptionally(new ServerNotSetupException());
            return future;
        }

        return request("/server").withServerToken(serverToken).sendAsync().thenApply(response -> {
            if(response.code() == 404) {
                throw new CompletionException(new ServerNotFoundException());
            } else if(response.code() == 429) {
                throw new CompletionException(new RateLimitException("You are being rate limited."));
            } else if(response.code() != 200) {
                throw new CompletionException(new IOException("Unexpected status code (" + response.code() + ")"));
            }

            try {
                JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                return GSON.fromJson(jsonObject.get("data"), ServerInformation.class);
            } catch (IOException e) {
                throw new CompletionException(new IOException("Unexpected response"));
            }
        });
    }

    /**
     * Sends a player session to the Analytics API for tracking.
     *
     * @param player The AnalysePlayer object representing the player to be tracked.
     * @return A CompletableFuture that indicates whether the operation was successful.
     */
    public CompletableFuture<Boolean> trackPlayerSession(AnalysePlayer player) {
        platform.getPlayers().remove(player.getUniqueId());

        if (getServerToken() == null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new ServerNotSetupException());
            return future;
        }

        if (! platform.isSetup()) {
            platform.debug("Skipped tracking player session for " + player.getName() + " as Analytics isn't setup.");
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.complete(false);
            return future;
        }

        if(platform.isPlayerExcluded(player.getUniqueId())) {
            platform.debug("Skipped tracking player session for " + player.getName() + " as they are excluded.");
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.complete(false);
            return future;
        }

        player.logout();
        platform.debug("Sending payload: " + GSON.toJson(player));

        if(player.getDurationInSeconds() < platform.getPlatformConfig().getMinimumPlaytime()) {
            platform.debug("Skipped tracking player session for " + player.getName() + " as they haven't played long enough.");
            platform.debug(
                    "They played for " + player.getDurationInSeconds() + " " + StringUtil.pluralise(player.getDurationInSeconds(), "second", "seconds")
                            + " but your minimum requirement is " + platform.getPlatformConfig().getMinimumPlaytime() + " " + StringUtil.pluralise(platform.getPlatformConfig().getMinimumPlaytime(), "second", "seconds") + "."
            );
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.complete(false);
            return future;
        }

        platform.debug("Tracking player session for " + player.getName() + "..");
        platform.debug(" - UUID: " + player.getUniqueId());
        platform.debug(" - Type: " + player.getType());
        platform.debug(" - Played for: " + player.getDurationInSeconds() + "s");
        platform.debug(" - IP: " + player.getIpAddress());
        platform.debug(" - Joined at: " + player.getJoinedAt());
        platform.debug(" - First joined at: " + player.getFirstJoinedAt());

        if(player.getStatistics().size() > 0) {
            platform.debug(" - Statistics:");
            player.getStatistics().forEach((key, value) -> platform.debug("   - %" + key + "%: " + value));
        } else {
            platform.debug(" - No statistics to track.");
        }

        return request("/server/sessions").withServerToken(serverToken).withBody(GSON.toJson(player)).sendAsync().thenApply(response -> {
            if(response.code() == 404) {
                throw new CompletionException(new ServerNotFoundException());
            } else if(response.code() == 429) {
                throw new CompletionException(new RateLimitException("You are being rate limited."));
            } else if(response.code() != 200) {
                throw new CompletionException(new IOException("Unexpected status code (" + response.code() + ")"));
            }

            try {
                JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                return jsonObject.get("success").getAsBoolean();
            } catch (IOException e) {
                throw new CompletionException(new IOException("Unexpected response"));
            }
        });
    }

    /**
     * Sends a setup completion request to the Analytics API.
     *
     * @return A CompletableFuture that indicates whether the operation was successful.
     */
    public CompletableFuture<Boolean> completeServerSetup() {
        if (getServerToken() == null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new ServerNotSetupException());
            return future;
        }

        return request("/server/setup").withServerToken(serverToken).sendAsync().thenApply(response -> {
            if(response.code() == 404) {
                throw new CompletionException(new ServerNotFoundException());
            } else if(response.code() == 429) {
                throw new CompletionException(new RateLimitException("You are being rate limited."));
            } else if(response.code() != 200) {
                throw new CompletionException(new IOException("Unexpected status code (" + response.code() + ")"));
            }

            try {
                JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                return jsonObject.get("success").getAsBoolean();
            } catch (IOException e) {
                throw new CompletionException(new IOException("Unexpected response"));
            }
        });
    }

    /**
     * Sends the current player count to the Analytics API in the form of a heartbeat.
     *
     * @param playerCount The number of players currently online.
     * @return A CompletableFuture that indicates whether the operation was successful.
     */
    public CompletableFuture<Boolean> trackHeartbeat(int playerCount) {
        if (getServerToken() == null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new ServerNotSetupException());
            return future;
        }

        JsonObject body = new JsonObject();
        body.addProperty("players", playerCount);

        return request("/server/heartbeat").withServerToken(serverToken).withBody(GSON.toJson(body)).sendAsync().thenApply(response -> {
            if(response.code() == 404) {
                throw new CompletionException(new ServerNotFoundException());
            } else if(response.code() == 429) {
                throw new CompletionException(new RateLimitException("You are being rate limited."));
            } else if(response.code() != 200) {
                throw new CompletionException(new IOException("Unexpected status code (" + response.code() + ")"));
            }

            try {
                JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                return jsonObject.get("success").getAsBoolean();
            } catch (IOException e) {
                throw new CompletionException(new IOException("Unexpected response"));
            }
        });
    }

    /**
     * Sends the current server telemetry to the Analytics API.
     *
     * @return A CompletableFuture that indicates whether the operation was successful.
     */
    public CompletableFuture<Boolean> sendTelemetry() {
        if (getServerToken() == null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new ServerNotSetupException());
            return future;
        }

        return request("/server/telemetry").withServerToken(serverToken).withBody(GSON.toJson(platform.getTelemetry())).sendAsync().thenApply(response -> {
            if(response.code() == 404) {
                throw new CompletionException(new ServerNotFoundException());
            } else if(response.code() == 429) {
                throw new CompletionException(new RateLimitException("You are being rate limited."));
            } else if(response.code() != 200) {
                throw new CompletionException(new IOException("Unexpected status code (" + response.code() + ")"));
            }

            try {
                JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                return jsonObject.get("success").getAsBoolean();
            } catch (IOException e) {
                throw new CompletionException(new IOException("Unexpected response"));
            }
        });
    }

    /**
     * Get the first page of a specified statistic leaderboard.
     *
     * @param leaderboard The leaderboard identifier
     * @return CompletableFuture containing the leaderboard data
     */
    public CompletableFuture<AnalyseLeaderboard> getLeaderboard(String leaderboard) {
        return getLeaderboard(leaderboard, 1);
    }

    /**
     * Get a specified page of a statistic leaderboard.
     *
     * @param leaderboard The leaderboard identifier
     * @param page The requested page number
     * @return CompletableFuture containing the leaderboard data
     */
    public CompletableFuture<AnalyseLeaderboard> getLeaderboard(String leaderboard, int page) {
        if (getServerToken() == null) {
            CompletableFuture<AnalyseLeaderboard> future = new CompletableFuture<>();
            future.completeExceptionally(new ServerNotSetupException());
            return future;
        }

        return request("/server/leaderboard/" + leaderboard + "?page=" + page).withServerToken(serverToken).sendAsync().thenApply(response -> {
            if(response.code() == 404) {
                throw new CompletionException(new ServerNotFoundException());
            } else if(response.code() == 429) {
                throw new CompletionException(new RateLimitException("You are being rate limited."));
            } else if(response.code() != 200) {
                throw new CompletionException(new IOException("Unexpected status code (" + response.code() + ")"));
            }

            try {
                JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                return GSON.fromJson(jsonObject.get("leaderboard"), AnalyseLeaderboard.class);
            } catch (IOException e) {
                throw new CompletionException(new IOException("Unexpected response"));
            }
        });
    }

    /**
     * Get information about a specific player by their name or UUID.
     *
     * @param id The player's name or UUID
     * @return CompletableFuture containing the player's profile data
     */
    public CompletableFuture<PlayerProfile> getPlayer(String id) {
        if (getServerToken() == null) {
            CompletableFuture<PlayerProfile> future = new CompletableFuture<>();
            future.completeExceptionally(new ServerNotSetupException());
            return future;
        }

        return request("/server/player/" + id).withServerToken(serverToken).sendAsync().thenApply(response -> {
            if(response.code() == 404) {
                throw new CompletionException(new ServerNotFoundException());
            } else if(response.code() == 429) {
                throw new CompletionException(new RateLimitException("You are being rate limited."));
            } else if(response.code() != 200) {
                throw new CompletionException(new IOException("Unexpected status code (" + response.code() + ")"));
            }

            try {
                JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                return GSON.fromJson(jsonObject.get("player"), PlayerProfile.class);
            } catch (IOException e) {
                throw new CompletionException(new IOException("Unexpected response"));
            }
        });
    }

    /**
     * Get the country code of a specific IP address.
     *
     * @param ip The IP address
     * @return CompletableFuture containing the country code
     * @deprecated This method is deprecated and may be removed in future versions
     */
    @Deprecated
    public CompletableFuture<String> getCountryFromIp(String ip) {
        if (getServerToken() == null) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(new ServerNotSetupException());
            return future;
        }

        return request("/ip/" + ip).withServerToken(serverToken).sendAsync().thenApply(response -> {
            if(response.code() == 404) {
                throw new CompletionException(new ServerNotFoundException());
            } else if(response.code() == 429) {
                throw new CompletionException(new RateLimitException("You are being rate limited."));
            } else if(response.code() != 200) {
                throw new CompletionException(new IOException("Unexpected status code (" + response.code() + ")"));
            }

            try {
                JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                if(! jsonObject.get("success").getAsBoolean()) return null;
                return jsonObject.get("country_code").getAsString();
            } catch (IOException e) {
                throw new CompletionException(new IOException("Unexpected response"));
            }
        });
    }

    /**
     * Get the server token associated with this SDK instance.
     *
     * @return The server token as a String
     */
    public String getServerToken() {
        return serverToken;
    }

    /**
     * Set the server token for this SDK instance.
     *
     * @param serverToken The server token as a String
     */
    public void setServerToken(String serverToken) {
        this.serverToken = serverToken;
    }

    /**
     * Create a new AnalyseRequest with the specified URL.
     *
     * @param url The URL to send the request to
     * @return An AnalyseRequest instance
     */
    public AnalyseRequest request(String url) {
        return new AnalyseRequest(API_URL + url, HTTP_CLIENT);
    }
}
