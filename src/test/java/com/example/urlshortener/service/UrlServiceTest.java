package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlCreateRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.dto.UrlUpdateRequest;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.exception.ResourceNotFoundException;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @InjectMocks
    private UrlServiceImpl urlService;

    private Url sampleUrl;

    @BeforeEach
    void setUp() {
        sampleUrl = new Url();
        sampleUrl.setUrl("https://example.com");
        sampleUrl.setShortCode("abc123");
        sampleUrl.setAccessCount(0L);
    }

    @Test
    void createUrl_success() {
        when(shortCodeGenerator.generate()).thenReturn("abc123");
        when(urlRepository.existsByShortCode("abc123")).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlCreateRequest request = new UrlCreateRequest("https://example.com");
        UrlResponse response = urlService.createUrl(request);

        assertNotNull(response);
        assertEquals("https://example.com", response.url());
        assertEquals("abc123", response.shortCode());
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    void getUrlByShortCode_success() {
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(sampleUrl));

        UrlResponse response = urlService.getUrlByShortCode("abc123");

        assertNotNull(response);
        assertEquals("https://example.com", response.url());
        assertEquals("abc123", response.shortCode());
    }

    @Test
    void getUrlByShortCode_notFound() {
        when(urlRepository.findByShortCode("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> urlService.getUrlByShortCode("unknown"));
    }

    @Test
    void updateUrl_success() {
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(sampleUrl));
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlUpdateRequest updateRequest = new UrlUpdateRequest("https://updated-example.com");
        UrlResponse response = urlService.updateUrl("abc123", updateRequest);

        assertNotNull(response);
        assertEquals("https://updated-example.com", response.url());
        assertEquals("abc123", response.shortCode());
    }

    @Test
    void deleteUrl_success() {
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(sampleUrl));

        urlService.deleteUrl("abc123");

        verify(urlRepository, times(1)).delete(sampleUrl);
    }

    @Test
    void getUrlStats_success() {
        sampleUrl.setAccessCount(5L);
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(sampleUrl));

        UrlStatsResponse stats = urlService.getUrlStats("abc123");

        assertNotNull(stats);
        assertEquals("https://example.com", stats.url());
        assertEquals(5L, stats.accessCount());
    }

    @Test
    void redirect_incrementsAccessCount() {
        sampleUrl.setAccessCount(2L);
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(sampleUrl));
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String redirectUrl = urlService.redirect("abc123");

        assertEquals("https://example.com", redirectUrl);
        assertEquals(3L, sampleUrl.getAccessCount());
        verify(urlRepository, times(1)).save(sampleUrl);
    }
}
