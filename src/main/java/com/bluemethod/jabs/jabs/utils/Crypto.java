package com.bluemethod.jabs.jabs.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Handles various cryptography functions, such as hashing
 */
public class Crypto {

    /**
     * Converts a string to an SHA-256 hash
     *
     * @param str the target string
     * @return 256 hash as a string
     */
    public static String sha256Hash(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
