package net.analyse.plugin.hook;


import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.request.response.AnalyseLeaderboard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlaceholderAPIExpansionHook extends PlaceholderExpansion {

    private final AnalysePlugin plugin;
    private final Map<String, CacheEntry> cache;

    public PlaceholderAPIExpansionHook(AnalysePlugin plugin) {
        this.plugin = plugin;
        this.cache = new HashMap<>();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "analyse";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        int lastUnderscoreIndex = params.lastIndexOf("_");

        if (lastUnderscoreIndex == -1) {
            return null;
        }

        String leaderboardId = params.substring(0, lastUnderscoreIndex);
        String field = params.substring(lastUnderscoreIndex + 1);
        int position;

        int secondLastUnderscoreIndex = leaderboardId.lastIndexOf("_");
        if (secondLastUnderscoreIndex == -1) {
            return null;
        }

        String positionString = leaderboardId.substring(secondLastUnderscoreIndex + 1);
        leaderboardId = leaderboardId.substring(0, secondLastUnderscoreIndex);

        try {
            position = Integer.parseInt(positionString) - 1;
        } catch (NumberFormatException e) {
            return "Invalid position";
        }

        if (position < 0) {
            return "Invalid position";
        }

        AnalyseLeaderboard leaderboard = getLeaderboard(leaderboardId);

        if (leaderboard == null || leaderboard.getData().size() <= position) {
            switch (field.toLowerCase()) {
                case "username":
                    return "?";
                case "value":
                    return Integer.toString(0);
                default:
                    return null;
            }
        }

        AnalyseLeaderboard.Player lbPlayer = leaderboard.getData().get(position);
        switch (field.toLowerCase()) {
            case "username":
                return lbPlayer.getName();
            case "value":
                return Integer.toString(lbPlayer.getValue());
            default:
                return null;
        }
    }

    private AnalyseLeaderboard getLeaderboard(String leaderboardId) {
        CacheEntry cacheEntry = cache.get(leaderboardId);
        long currentTime = System.currentTimeMillis();

        if (cacheEntry == null || currentTime - cacheEntry.timestamp > TimeUnit.MINUTES.toMillis(5)) {
            plugin.getSDK().getLeaderboard(leaderboardId).thenAccept(leaderboard -> {
                cache.put(leaderboardId, new CacheEntry(leaderboard, System.currentTimeMillis()));
            });
            return null; // Return null while the new leaderboard data is being fetched
        }

        return cacheEntry.leaderboard;
    }

    private static class CacheEntry {
        private final AnalyseLeaderboard leaderboard;
        private final long timestamp;

        public CacheEntry(AnalyseLeaderboard leaderboard, long timestamp) {
            this.leaderboard = leaderboard;
            this.timestamp = timestamp;
        }
    }
}