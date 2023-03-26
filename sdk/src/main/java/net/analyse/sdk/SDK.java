package net.analyse.sdk;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.analyse.sdk.obj.AnalysePlayer;
import net.analyse.sdk.platform.Platform;
import net.analyse.sdk.platform.PlatformType;
import net.analyse.sdk.request.AnalyseRequest;
import net.analyse.sdk.request.exception.AnalyseException;
import net.analyse.sdk.request.exception.ServerNotFoundException;
import net.analyse.sdk.request.response.AnalyseLeaderboard;
import net.analyse.sdk.request.response.PlayerProfile;
import net.analyse.sdk.request.response.PluginInformation;
import net.analyse.sdk.request.response.ServerInformation;
import net.analyse.sdk.util.StringUtil;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;

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
     * Get the server information from the API.
     * @return The server information
     */
    public CompletableFuture<PluginInformation> getPluginVersion(PlatformType type) {
        return request("/plugin").sendAsync()
                .thenApply(response -> {
                    try {
                        JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                        JsonObject versionData = jsonObject.get("version").getAsJsonObject();
                        JsonObject assetData = jsonObject.get("assets").getAsJsonObject();

                        return new PluginInformation(
                                versionData.get("name").getAsString(),
                                versionData.get("incremental").getAsInt(),
                                assetData.get(type.name().toLowerCase()).getAsString()
                        );
                    } catch (IOException e) {
                        platform.log(Level.WARNING, "Failed to get plugin version: " + e.getMessage());
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    /**
     * Get the server information from the API.
     * @return The server information
     */
    public CompletableFuture<ServerInformation> getServerInformation() {
        if (getServerToken() == null) {
            CompletableFuture<ServerInformation> future = new CompletableFuture<>();
            future.completeExceptionally(new AnalyseException("Analyse is not setup!"));
            return future;
        }

        return request("/server").withServerToken(serverToken).sendAsync()
                .thenApply(response -> {
                    if (response.code() == 404) throw new CompletionException(new ServerNotFoundException("Server not found"));

                    try {
                        JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                        return GSON.fromJson(jsonObject.get("data"), ServerInformation.class);
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                }).exceptionally(throwable -> {
                    Throwable cause = throwable.getCause();
                    if (!(cause instanceof ServerNotFoundException) && !(cause instanceof AnalyseException)) {
                        platform.log(Level.WARNING, "Failed to get server information: " + cause.getMessage());
                    }
                    throw new CompletionException(cause);
                });
    }

    /**
     * Send a player session to the API.
     * @param player The player to track
     * @throws AnalyseException Thrown if the server is not setup or the request fails
     */
    public void trackPlayerSession(AnalysePlayer player) throws AnalyseException, ServerNotFoundException {
        platform.getPlayers().remove(player.getUniqueId());

        if (! platform.isSetup()) {
            platform.debug("Skipped tracking player session for " + player.getName() + " as Analyse isn't setup.");
            return;
        }

        player.logout();

        platform.debug("Sending payload: " + GSON.toJson(player));

        if(player.getDurationInSeconds() < platform.getPlatformConfig().getMinimumPlaytime()) {
            platform.debug("Skipped tracking player session for " + player.getName() + " as they haven't played long enough.");
            platform.debug(
                    "They played for " + player.getDurationInSeconds() + " " + StringUtil.pluralise(player.getDurationInSeconds(), "second", "seconds")
                            + " but your minimum requirement is " + platform.getPlatformConfig().getMinimumPlaytime() + " " + StringUtil.pluralise(platform.getPlatformConfig().getMinimumPlaytime(), "second", "seconds") + "."
            );
            return;
        }

        platform.debug("Tracking player session for " + player.getName() + "..");
        platform.debug(" - UUID: " + player.getUniqueId());
        platform.debug(" - Played for: " + player.getDurationInSeconds() + "s");
        platform.debug(" - IP: " + player.getIpAddress());
        platform.debug(" - Joined at: " + player.getJoinedAt());

        try (Response response = request("/server/sessions").withServerToken(serverToken).withBody(GSON.toJson(player)).send()) {
            if(response.code() == 404) throw new ServerNotFoundException("Server not found");
            if(response.code() != 200) {
                if(!response.message().isEmpty()) {
                    throw new AnalyseException(response.message());
                }

                throw new AnalyseException(String.valueOf(response.code()));
            }

            platform.debug("Successfully tracked player session for " + player.getName() + ".");
        } catch (IOException e) {
            platform.log(Level.WARNING, "Failed to track session for " + player.getName() + ": " + e.getMessage());
        }
    }

    public AnalyseLeaderboard getLeaderboard(String leaderboard) throws AnalyseException, ServerNotFoundException {
        return getLeaderboard(leaderboard, 1);
    }

    public AnalyseLeaderboard getLeaderboard(String leaderboard, int page) throws AnalyseException, ServerNotFoundException {
        if (!platform.isSetup()) throw new AnalyseException("Analyse is not setup!");

        try (Response response = request("/server/leaderboard/" + leaderboard + "?page=" + page).withServerToken(serverToken).send()) {
            if(response.code() == 404) throw new ServerNotFoundException("Leaderboard not found");
            if(response.code() != 200) {
                if(!response.message().isEmpty()) {
                    throw new AnalyseException(response.message());
                }

                throw new AnalyseException(String.valueOf(response.code()));
            }

            platform.debug("Successfully got leaderboard " + leaderboard + ".");

            try {
                JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                return GSON.fromJson(jsonObject.get("leaderboard"), AnalyseLeaderboard.class);
            } catch (IOException e) {
                throw new AnalyseException("Failed to convert leaderboard to object: " + e.getMessage());
            }
        } catch (IOException e) {
            platform.log(Level.WARNING, "Failed to get leaderboard " + leaderboard + ": " + e.getMessage());
        }
        return null;
    }

    public PlayerProfile getPlayer(String id) throws AnalyseException, ServerNotFoundException {
        if (!platform.isSetup()) throw new AnalyseException("Analyse is not setup!");

        try (Response response = request("/server/player/" + id).withServerToken(serverToken).send()) {
            if(response.code() == 404) throw new ServerNotFoundException("Player not found");
            if(response.code() != 200) {
                if(!response.message().isEmpty()) {
                    throw new AnalyseException(response.message());
                }

                throw new AnalyseException(String.valueOf(response.code()));
            }

            platform.debug("Successfully got player " + id + ".");

            try {
                JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                return GSON.fromJson(jsonObject.get("player"), PlayerProfile.class);
            } catch (IOException e) {
                throw new AnalyseException("Failed to convert player to object: " + e.getMessage());
            }
        } catch (IOException e) {
            platform.log(Level.WARNING, "Failed to get player " + id + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Send the current player count to the API.
     * @param playerCount The current player count
     */
    public CompletableFuture<Boolean> trackHeartbeat(int playerCount) {
        if (getServerToken() == null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new AnalyseException("Analyse is not setup!"));
            return future;
        }

        JsonObject body = new JsonObject();
        body.addProperty("players", playerCount);

        return request("/server/heartbeat").withServerToken(serverToken).withBody(GSON.toJson(body)).sendAsync()
                .thenApply(response -> {
                    if (response.code() == 404) throw new CompletionException(new ServerNotFoundException("Server not found"));

                    try {
                        JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                        return jsonObject.get("success").getAsBoolean();
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                }).exceptionally(throwable -> {
                    Throwable cause = throwable.getCause();
                    if (!(cause instanceof ServerNotFoundException) && !(cause instanceof AnalyseException)) {
                        platform.log(Level.WARNING, "Failed to get send heartbeat: " + throwable.getMessage());
                    }

                    throw new CompletionException(cause);
                });
    }

    /**
     * Sets the server token
     * @param serverToken The server token
     */
    public void setServerToken(String serverToken) {
        this.serverToken = serverToken;
    }

    /**
     * Get the server token
     * @return The server token
     */
    public String getServerToken() {
        return serverToken;
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
