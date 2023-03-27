package net.analyse.sdk.obj;

public class PlayerStatistic {
    private final String key;
    private final Object value;

    /**
     * Creates a new PlayerStatistic
     * @param key The key of the statistic
     * @param value The value of the statistic
     */
    public PlayerStatistic(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key of the statistic
     * @return The key of the statistic
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the value of the statistic
     * @return The value of the statistic
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns a string representation of the statistic
     * @return A string representation of the statistic
     */
    @Override
    public String toString() {
        return "PlayerStatistic{" + "key=" + key + ", value=" + value + '}';
    }
}
