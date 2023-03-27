package net.analyse.sdk.obj;

import com.google.common.collect.Maps;

import java.util.Date;
import java.util.Map;

/**
 * The player event object.
 */
public class PlayerEvent {
    private final String origin;
    private final String id;
    private final Date happenedAt;
    private final Map<String, Object> metadata;

    /**
     * Creates a new player event.
     * @param origin The origin of the event (e.g. "Tebex", "Analyse", "CurseForge")
     * @param id The id of the event (e.g. "purchase", "login", "join")
     */
    public PlayerEvent(String origin, String id) {
        this.origin = origin.replace(" ", "_");
        this.id = id.replace(" ", "_");
        this.happenedAt = new Date();
        this.metadata = Maps.newHashMap();
    }

    /**
     * Creates a new player event.
     * @param origin The origin of the event (e.g. "Tebex", "Analyse", "CurseForge")
     * @param id The id of the event (e.g. "purchase", "login", "join")
     * @param happenedAt The date the event happened
     */
    public PlayerEvent(String origin, String id, Date happenedAt) {
        this.origin = origin.replace(" ", "_");
        this.id = id.replace(" ", "_");
        this.happenedAt = happenedAt;
        this.metadata = Maps.newHashMap();
    }

    /**
     * Adds metadata to the event.
     * @param key The key of the metadata
     * @param value The value of the metadata
     * @return The event object
     */
    public PlayerEvent withMetadata(String key, Object value) {
        this.metadata.put(key.replace(" ", "_"), value);
        return this;
    }

    /**
     * Gets the origin of the event.
     * @return The origin of the event
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Gets the id of the event
     * @return The id of the event
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the date the event happened
     * @return The date the event happened
     */
    public Date getHappenedAt() {
        return happenedAt;
    }

    /**
     * Gets the metadata of the event.
     * @return The metadata of the event
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Returns a string representation of the event
     * @return A string representation of the event
     */
    @Override
    public String toString() {
        return "PlayerEvent{" +
                "id='" + id + '\'' +
                ", happenedAt=" + happenedAt +
                ", metadata=" + metadata +
                '}';
    }
}
