package net.analyse.sdk.obj;

public class PlayerStatistic {
    private String key;
    private Object value;

    public PlayerStatistic(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
