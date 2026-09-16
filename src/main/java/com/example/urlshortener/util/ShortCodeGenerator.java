package com.example.urlshortener.util;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class ShortCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 6;
    private final SecureRandom random = new SecureRandom();
    public String generate() { StringBuilder result = new StringBuilder(LENGTH); for (int i = 0; i < LENGTH; i++) result.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length()))); return result.toString(); }
}
