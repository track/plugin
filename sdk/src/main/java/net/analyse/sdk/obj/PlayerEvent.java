package net.analyse.sdk.obj;

import com.google.common.collect.Maps;

import java.util.Date;
import java.util.Map;

public class PlayerEvent {
    private final String id;
    private final String origin;
    private final Date happenedAt;
    private final Map<String, Object> metadata;

    public PlayerEvent(String id, String origin) {
        this.id = id.replace(" ", "_");
        this.origin = origin;
        this.happenedAt = new Date();
        this.metadata = Maps.newHashMap();
    }

    public PlayerEvent(String id, String origin, Date happenedAt) {
        this.id = id.replace(" ", "_");
        this.origin = origin;
        this.happenedAt = happenedAt;
        this.metadata = Maps.newHashMap();
    }

    public PlayerEvent withMetadata(String key, Object value) {
        this.metadata.put(key.replace(" ", "_"), value);
        return this;
    }

    public String getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public Date getHappenedAt() {
        return happenedAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "PlayerEvent{" +
                "id='" + id + '\'' +
                ", origin='" + origin + '\'' +
                ", happenedAt=" + happenedAt +
                ", metadata=" + metadata +
                '}';
    }
}
