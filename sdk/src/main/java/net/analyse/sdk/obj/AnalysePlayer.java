package net.analyse.sdk.obj;

import net.analyse.sdk.platform.PlayerType;

import java.util.*;

/**
 * The player object
 */
public class AnalysePlayer {
    private final String name;
    private final UUID uuid;
    private final Date joinedAt;
    private final String ipAddress;
    private Date firstJoinedAt;
    private Date quitAt;
    private PlayerType type;
    private String domain;
    private final List<PlayerStatistic> statistics;
    private final List<PlayerEvent> events;

    public AnalysePlayer(String name, UUID uuid, String ipAddress) {
        this.name = name;
        this.uuid = uuid;
        this.joinedAt = new Date();
        this.quitAt = null;
        this.ipAddress = ipAddress;
        this.statistics = new ArrayList<>();
        this.events = new ArrayList<>();
        this.type = PlayerType.JAVA;
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public Date getFirstJoinedAt() {
        return firstJoinedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public List<PlayerStatistic> getStatistics() {
        return statistics;
    }

    public List<PlayerEvent> getEvents() {
        return events;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

    /**
     * Calculate the players session
     * @param quitAt The date the player quit
     * @return The session in seconds
     */
    public int getDurationInSeconds(Date quitAt) {
        return (int) ((quitAt.getTime() - joinedAt.getTime()) / 1000);
    }

    /**
     * Calculate the players session
     * @return The session in seconds
     */
    public int getDurationInSeconds() {
        return this.quitAt != null ? getDurationInSeconds(this.quitAt) : getDurationInSeconds(new Date());
    }

    /**
     * Logout the player and set the quit date
     */
    public void logout() {
        this.quitAt = new Date();
    }

    /**
     * Track a custom event
     * @param event The event to track
     */
    public void track(PlayerEvent... event) {
        Arrays.stream(event).forEach(evt -> getEvents().add(evt));
    }

    public void setFirstJoinedAt(Date firstJoinedAt) {
        this.firstJoinedAt = firstJoinedAt;
    }

    @Override
    public String toString() {
        return "AnalysePlayer{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                ", joinedAt=" + joinedAt +
                ", ipAddress='" + ipAddress + '\'' +
                ", firstJoinedAt=" + firstJoinedAt +
                ", quitAt=" + quitAt +
                ", type=" + type +
                ", domain='" + domain + '\'' +
                ", statistics=" + statistics +
                ", events=" + events +
                '}';
    }
}