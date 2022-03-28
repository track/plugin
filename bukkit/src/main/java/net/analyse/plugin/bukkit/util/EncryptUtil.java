package net.analyse.plugin.bukkit.util;

import java.security.SecureRandom;
import java.util.Random;

public class EncryptUtil {
    // TODO: Could this be replaced with a standardized key generator?
    public static String generateEncryptionKey(int length){
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        int n = alphabet.length();

        StringBuilder result = new StringBuilder();
        Random r = new SecureRandom();

        for (int i = 0; i < length; i++)
            result.append(alphabet.charAt(r.nextInt(n)));

        return result.toString();
    }
}
