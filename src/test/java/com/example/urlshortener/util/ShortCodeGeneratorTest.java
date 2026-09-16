package com.example.urlshortener.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    private final ShortCodeGenerator generator = new ShortCodeGenerator();

    @Test
    void testGenerateReturnsSixCharacterAlphanumericCode() {
        String code = generator.generate();
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("^[a-zA-Z0-9]{6}$"));
    }

    @Test
    void testGenerateUniqueness() {
        String code1 = generator.generate();
        String code2 = generator.generate();
        assertNotEquals(code1, code2);
    }
}
