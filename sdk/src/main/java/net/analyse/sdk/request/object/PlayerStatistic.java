package net.analyse.sdk.request.object;

import org.jetbrains.annotations.NotNull;

public class PlayerStatistic {

    private final String key;
    private final Object value;

    public PlayerStatistic(final @NotNull String key, final @NotNull Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PlayerStatistic{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
