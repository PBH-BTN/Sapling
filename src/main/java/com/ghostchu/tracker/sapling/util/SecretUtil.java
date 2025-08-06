package com.ghostchu.tracker.sapling.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class SecretUtil {
    public static String hashPassword(String password) {
        String salt = Hashing.crc32c().hashString(password, StandardCharsets.ISO_8859_1).toString();
        return Hashing.sha512().hashString(password + salt, StandardCharsets.ISO_8859_1).toString();
    }
}
