package com.ZenFin.security;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public  String keyGen() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32]; // 256 bits = 32 bytes
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}