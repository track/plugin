package net.analyse.plugin.util;

import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;

public class GeyserUtil {
    public static boolean isBedrockPlayer(UUID uuid) {
        return GeyserApi.api().isBedrockPlayer(uuid);
    }
}
