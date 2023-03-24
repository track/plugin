package net.analyse.sdk.request.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PlayerProfile {
    private final String name;
    private final UUID uuid;
    private final Date createdAt;
    private final Date updatedAt;
    private final Date firstJoinedAt;
    private final Date lastLoggedInAt;
    private final int totalSessionTime;

    private final List<Statistic> statistics;

    public PlayerProfile(String name, UUID uuid, Date createdAt, Date updatedAt, Date firstJoinedAt, Date lastLoggedInAt, int totalSessionTime, List<Statistic> statistics) {
        this.name = name;
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.firstJoinedAt = firstJoinedAt;
        this.lastLoggedInAt = lastLoggedInAt;
        this.totalSessionTime = totalSessionTime;
        this.statistics = statistics;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getFirstJoinedAt() {
        return firstJoinedAt;
    }

    public Date getLastLoggedInAt() {
        return lastLoggedInAt;
    }

    public int getTotalSessionTime() {
        return totalSessionTime;
    }

    public List<Statistic> getStatistics() {
        return statistics;
    }

    public static class Statistic {
        private final String nickname;
        private final String placeholder;
        private final Object value;

        public Statistic(String nickname, String placeholder, Object value) {
            this.nickname = nickname;
            this.placeholder = placeholder;
            this.value = value;
        }

        public String getNickname() {
            return nickname;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public Object getValue() {
            return value;
        }
    }
}