package net.analyse.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.analyse.sdk.exception.InvalidIPAddressException;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.request.APIRequest;
import net.analyse.sdk.request.impl.PlayerSessionRequest;
import net.analyse.sdk.request.impl.ServerHeartbeatRequest;
import net.analyse.sdk.request.object.PlayerStatistic;
import net.analyse.sdk.response.GetPluginResponse;
import net.analyse.sdk.response.GetServerResponse;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

public class AnalyseSDK {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    private final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    private final String token;
    private final String encryptionKey;
    private final String baseUrl;

    private final List<UUID> excludedPlayers = new ArrayList<>();
    private final Map<UUID, List<PlayerStatistic>> playerStatistics = new HashMap<>();

    /**
     * @param token The token of the server.
     * @param encryptionKey The encryption key of the server.
     */
    public AnalyseSDK(String token, String encryptionKey) {
        this.token = token;
        this.encryptionKey = encryptionKey;
        this.baseUrl = "https://app.analyse.net/api/v1/";
        AnalyseCore.setCore(this);
    }

    /**
     * @param token The token of the server.
     * @param encryptionKey The encryption key of the server.
     * @param baseUrl The base url of the api endpoint.
     */
    public AnalyseSDK(String token, String encryptionKey, String baseUrl) {
        this.token = token;
        this.encryptionKey = encryptionKey;
        this.baseUrl = baseUrl;
        AnalyseCore.setCore(this);
    }

    /**
     * @return The excluded players.
     */
    public List<UUID> getExcludedPlayers() {
        return excludedPlayers;
    }

    /**
     * @return The player statistics.
     */
    public Map<UUID, List<PlayerStatistic>> getPlayerStatistics() {
        return playerStatistics;
    }

    /**
     * @param player The player to get the statistics for.
     * @return The player statistics.
     */
    public List<PlayerStatistic> getPlayerStatistics(UUID player) {
        if (!playerStatistics.containsKey(player)) {
            playerStatistics.put(player, new ArrayList<>());
        }
        return playerStatistics.get(player);
    }

    /**
     * @return The server token.
     */
    public String getToken() {
        return token;
    }

    /**
     * @return The api base url.
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * @return The server information.
     * @throws ServerNotFoundException
     */
    public GetServerResponse getServer() throws ServerNotFoundException {
        Response response = new APIRequest(baseUrl + "server", HTTP_CLIENT)
                .withServerToken(this.token)
                .send();

        if(response.code() == 404) {
            response.close();
            throw new ServerNotFoundException();
        }

        GetServerResponse getServerResponse = null;
        try {
            JsonObject bodyJson = GSON.fromJson(response.body().string(), JsonObject.class);
            final JsonObject serverJson = bodyJson.getAsJsonObject("data");
            final JsonObject teamQuotaJson = serverJson.getAsJsonObject("team_quota");

            getServerResponse = new GetServerResponse(
                    serverJson.get("name").getAsString(),
                    serverJson.get("uuid").getAsString(),
                    Instant.parse(serverJson.get("created_at").getAsString()),
                    teamQuotaJson.get("current").getAsInt(),
                    teamQuotaJson.get("limit").getAsInt()
            );
        } catch (IOException e) {
            // TODO: Handle this.
            e.printStackTrace();
        }

        response.close();

        return getServerResponse;
    }

    /**
     * @return The plugin information.
     */
    public GetPluginResponse getPluginVersion() {
        Response response = new APIRequest(baseUrl + "plugin", HTTP_CLIENT)
                .send();

        GetPluginResponse getPluginResponse = null;
        try {
            JsonObject bodyJson = GSON.fromJson(response.body().string(), JsonObject.class);
            final JsonObject versionData = bodyJson.getAsJsonObject("version");
            final JsonObject assetData = bodyJson.getAsJsonObject("assets");

            getPluginResponse =new GetPluginResponse(
                    versionData.get("name").getAsString(),
                    versionData.get("incremental").getAsInt(),
                    assetData.get("bukkit").getAsString(),
                    assetData.get("bungee").getAsString(),
                    assetData.get("velocity").getAsString()
            );
        } catch (IOException e) {
            // TODO: Handle this.
            e.printStackTrace();
        }

        response.close();

        return getPluginResponse;
    }

    /**
     * @param players The total players.
     * @throws ServerNotFoundException
     */
    public void sendHeartbeat(int players) throws ServerNotFoundException {
        ServerHeartbeatRequest serverHeartbeatRequest = new ServerHeartbeatRequest(players);

        Response response = new APIRequest(baseUrl + "server/heartbeat", HTTP_CLIENT)
                .withServerToken(this.token)
                .withPayload(serverHeartbeatRequest.toJson())
                .send();

        response.close();

        if(response.code() == 404) {
            throw new ServerNotFoundException();
        }
    }

    /**
     * @param ipAddress The ip address of the player.
     * @return The player country.
     * @throws ServerNotFoundException
     * @throws InvalidIPAddressException
     */
    public String getLocationFromIP(String ipAddress) throws ServerNotFoundException, InvalidIPAddressException {
        Response response = new APIRequest(baseUrl + "ip/" + ipAddress, HTTP_CLIENT)
                .withServerToken(this.token)
                .send();

        if(response.code() == 404) {
            response.close();
            throw new ServerNotFoundException();
        }

        String countryCode = null;
        try {
            JsonObject bodyJson = GSON.fromJson(response.body().string(), JsonObject.class);
            boolean successful = bodyJson.get("success").getAsBoolean();

            if(! successful)
            {
                throw new InvalidIPAddressException(ipAddress);
            }

            countryCode = bodyJson.get("country_code").getAsString();
        } catch (IOException e) {
            // TODO: Handle this.
            e.printStackTrace();
        }

        response.close();

        return countryCode;
    }

    /**
     * @param uuid The uuid of the player.
     * @param username The username of the player.
     * @param joinedAt The time the player joined the server.
     * @param domain The domain the player joined from.
     * @param ipAddress The ip address of the player.
     * @param firstJoinDate The first date the player joined (if any).
     * @param playerStatistics The player statistics.
     * @throws ServerNotFoundException
     */
    public void sendPlayerSession(@NotNull UUID uuid, @NotNull String username, @NotNull Date joinedAt, String domain, String ipAddress, Date firstJoinDate, List<PlayerStatistic> playerStatistics) throws ServerNotFoundException {
        String ipCountry;

        try {
            ipCountry = getLocationFromIP(ipAddress);
        } catch (InvalidIPAddressException exception) {
            ipCountry = null;
        }

        PlayerSessionRequest playerSessionRequest = new PlayerSessionRequest(
                uuid,
                username,
                joinedAt,
                new Date(),
                domain,
                ipCountry != null ? hashIp(ipAddress) : null,
                ipCountry,
                firstJoinDate,
                playerStatistics
        );

        Response response = new APIRequest(baseUrl + "server/sessions", HTTP_CLIENT)
                .withPayload(playerSessionRequest.toJson())
                .withServerToken(this.token)
                .send();

        response.close();
    }

    /**
     * @param ipAddress The ip address of the player.
     * @return The hashed ip address.
     */
    public String hashIp(@NotNull String ipAddress) {
        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(this.encryptionKey.getBytes());
            byte[] bytes = md.digest(ipAddress.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generatedPassword;
    }
}
