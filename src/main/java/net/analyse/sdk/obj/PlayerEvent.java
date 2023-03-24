package net.analyse.sdk.obj;

import com.google.common.collect.Maps;

import java.util.Date;
import java.util.Map;

public class PlayerEvent {
    private final String id;
    private final Date happenedAt;
    private final Map<String, Object> metadata;

    public PlayerEvent(String id) {
        this.id = id.replace(" ", "_");
        this.happenedAt = new Date();
        this.metadata = Maps.newHashMap();
    }

    public PlayerEvent(String id, Date happenedAt) {
        this.id = id.replace(" ", "_");
        this.happenedAt = happenedAt;
        this.metadata = Maps.newHashMap();
    }

    public PlayerEvent withMetadata(String key, Object value) {
        this.metadata.put(key.replace(" ", "_"), value);
        return this;
    }
}
