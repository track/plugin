package net.analyse.plugin.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class EncryptUtil {

    public static String toSHA256(String password, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    // TODO: Could this be replaced with a standardized key generator?
    public static String generateEncryptionKey(int length){
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; // 9

        int n = alphabet.length(); // 10

        StringBuilder result = new StringBuilder();
        Random r = new SecureRandom(); // 11

        for (int i = 0; i < length; i++) // 12
            result.append(alphabet.charAt(r.nextInt(n))); //13

        return result.toString();
    }
}
