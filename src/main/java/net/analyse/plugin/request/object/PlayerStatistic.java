package net.analyse.plugin.request.object;

import org.jetbrains.annotations.NotNull;

public class PlayerStatistic {

    private final String key;
    private final Object value;

    public PlayerStatistic(final @NotNull String key, final @NotNull Object value) {
        this.key = key;
        this.value = value;
    }
}
