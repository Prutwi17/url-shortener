package com.example.urlshortener.validation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UrlValidatorTest {

    private final UrlValidator validator = new UrlValidator();

    @Test
    void testValidHttpUrl() {
        assertTrue(validator.isValid("http://example.com", null));
        assertTrue(validator.isValid("http://example.com/path?query=1", null));
    }

    @Test
    void testValidHttpsUrl() {
        assertTrue(validator.isValid("https://example.com", null));
        assertTrue(validator.isValid("https://sub.domain.org/test", null));
    }

    @Test
    void testInvalidUrls() {
        assertFalse(validator.isValid(null, null));
        assertFalse(validator.isValid("", null));
        assertFalse(validator.isValid("   ", null));
        assertFalse(validator.isValid("ftp://example.com", null));
        assertFalse(validator.isValid("javascript:alert(1)", null));
        assertFalse(validator.isValid("example.com", null));
        assertFalse(validator.isValid("http://", null));
    }
}
