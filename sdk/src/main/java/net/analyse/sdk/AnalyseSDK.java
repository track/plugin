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
import net.analyse.sdk.response.GetServerResponse;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AnalyseSDK {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    private final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    private final String token;
    private final String encryptionKey;
    private final String baseUrl;

    public AnalyseSDK(String token, String encryptionKey) {
        this.token = token;
        this.encryptionKey = encryptionKey;
        this.baseUrl = "https://app.analyse.net/api/v1/";
    }

    public AnalyseSDK(String token, String encryptionKey, String baseUrl) {
        this.token = token;
        this.encryptionKey = encryptionKey;
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

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
            Boolean successful = bodyJson.get("success").getAsBoolean();

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

    public void sendPlayerSession(@NotNull UUID uuid, @NotNull String username, @NotNull Date joinedAt, String domain, String ipAddress, List<PlayerStatistic> playerStatistics) throws ServerNotFoundException {
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
                playerStatistics
        );

        Response response = new APIRequest(baseUrl + "server/sessions", HTTP_CLIENT)
                .withPayload(playerSessionRequest.toJson())
                .withServerToken(this.token)
                .send();

        response.close();
    }

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
