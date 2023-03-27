package net.analyse.sdk.util;

/**
 * String utility methods.
 */
public class StringUtil {
    /**
     * Pluralise a string based on a count.
     * @param count The count to base the pluralisation on.
     * @param singular The singular form of the string.
     * @param plural The plural form of the string.
     * @return The singular or plural form of the string.
     */
    public static String pluralise(int count, String singular, String plural) {
        return count == 1 ? singular : plural;
    }
}
