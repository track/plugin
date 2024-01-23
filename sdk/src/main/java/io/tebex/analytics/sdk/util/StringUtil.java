package io.tebex.analytics.sdk.util;

public class StringUtil {
    public static String pluralise(int count, String singular, String plural) {
        return count == 1 ? singular : plural;
    }
}
