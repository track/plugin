package io.tebex.analytics.sdk.redis.data;


public final class DataSet {

    private final String key;
    private final Object value;

    public DataSet(final String key, final Object value) {
        this.key = key;
        this.value = value;
    }

    public String key() {
        return this.key;
    }

    public Object value() {
        return this.value;
    }

}