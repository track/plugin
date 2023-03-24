package net.analyse.sdk.request.response;

import java.util.List;
import java.util.UUID;

public class AnalyseLeaderboard {
    private final int currentPage;
    private final List<Player> data;
    private final int total;

    public AnalyseLeaderboard(int currentPage, List<Player> data, int total) {
        this.currentPage = currentPage;
        this.data = data;
        this.total = total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<Player> getData() {
        return data;
    }

    public int getTotal() {
        return total;
    }

    public static class Player {
        private final String name;
        private final UUID uuid;
        private final int value;

        public Player(String name, UUID uuid, int value) {
            this.name = name;
            this.uuid = uuid;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public UUID getUniqueId() {
            return uuid;
        }

        public int getValue() {
            return value;
        }
    }
}
