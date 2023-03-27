package net.analyse.sdk;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.exception.ServerNotSetupException;
import net.analyse.sdk.obj.AnalysePlayer;
import net.analyse.sdk.platform.Platform;
import net.analyse.sdk.platform.PlatformType;
import net.analyse.sdk.request.AnalyseRequest;
import net.analyse.sdk.request.response.AnalyseLeaderboard;
import net.analyse.sdk.request.response.PlayerProfile;
import net.analyse.sdk.request.response.PluginInformation;
import net.analyse.sdk.request.response.ServerInformation;
import net.analyse.sdk.util.StringUtil;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class SDK {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    private final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().retryOnConnectionFailure(true).build();

    private final int API_VERSION = 1;
    private final String API_URL = String.format("https://app.analyse.net/api/v%d", API_VERSION);

    private final Platform platform;
    private String serverToken;

    public SDK(Platform platform, String serverToken) {
        this.platform = platform;
        this.serverToken = serverToken;
    }

    /**
     * Get the latest plugin information
     * @return PluginInformation
     */
    public CompletableFuture<PluginInformation> getPluginVersion(PlatformType platformType) {
        return request("/plugin").sendAsync().thenApply(response -> {
            if(response.code() == 404) {
                throw new CompletionException(new ServerNotFoundException());
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
     * Get information about a server
     * @return ServerInformation
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
     * Send a player session to Analyse
     * @param player The player to track
     * @return If successful
     */
    public CompletableFuture<Boolean> trackPlayerSession(AnalysePlayer player) {
        platform.getPlayers().remove(player.getUniqueId());

        if (getServerToken() == null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new ServerNotSetupException());
            return future;
        }

        if (! platform.isSetup()) {
            platform.debug("Skipped tracking player session for " + player.getName() + " as Analyse isn't setup.");
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
        platform.debug(" - Played for: " + player.getDurationInSeconds() + "s");
        platform.debug(" - IP: " + player.getIpAddress());
        platform.debug(" - Joined at: " + player.getJoinedAt());

        return request("/server/sessions").withServerToken(serverToken).withBody(GSON.toJson(player)).sendAsync().thenApply(response -> {
            if(response.code() == 404) {
                throw new CompletionException(new ServerNotFoundException());
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
     * Send the current player count to Analyse
     * @param playerCount The player count
     * @return If successful
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
     * Get a statistic leaderboard for the first page
     * @param leaderboard The leaderboard
     * @return AnalyseLeaderboard
     */
    public CompletableFuture<AnalyseLeaderboard> getLeaderboard(String leaderboard) {
        return getLeaderboard(leaderboard, 1);
    }

    /**
     * Get a statistic leaderboard on a specific page
     * @param leaderboard The leaderboard
     * @param page The page
     * @return AnalyseLeaderboard
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
     * Get information about a specific player
     * @param id The player name/uuid
     * @return PlayerProfile
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
     * Get the server token
     * @return The server token
     */
    public String getServerToken() {
        return serverToken;
    }

    /**
     * Sets the server token
     * @param serverToken The server token
     */
    public void setServerToken(String serverToken) {
        this.serverToken = serverToken;
    }

    /**
     * Send a new Analyse request
     * @param url The URL to send the request to
     * @return The request
     */
    public AnalyseRequest request(String url) {
        return new AnalyseRequest(API_URL + url, HTTP_CLIENT);
    }
}
