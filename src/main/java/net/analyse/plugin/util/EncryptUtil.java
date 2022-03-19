package net.analyse.plugin.util;

import java.security.SecureRandom;
import java.util.Random;

public class EncryptUtil {

    private static final Random RANDOM = new SecureRandom();
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // TODO: Could this be replaced with a standardized key generator?
    public static String generateEncryptionKey(int length){
        final int characterLength = CHARACTERS.length();
        final StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++)
            result.append(CHARACTERS.charAt(RANDOM.nextInt(characterLength)));

        return result.toString();
    }
}
