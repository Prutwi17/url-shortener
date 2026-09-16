package com.example.urlshortener.controller;

import com.example.urlshortener.dto.UrlCreateRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.dto.UrlUpdateRequest;
import com.example.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlCreateRequest request) {
        UrlResponse response = urlService.createUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/shorten/{shortCode}")
    public ResponseEntity<UrlResponse> getShortUrl(@PathVariable String shortCode) {
        UrlResponse response = urlService.getUrlByShortCode(shortCode);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/shorten/{shortCode}")
    public ResponseEntity<UrlResponse> updateShortUrl(
            @PathVariable String shortCode,
            @Valid @RequestBody UrlUpdateRequest request) {
        UrlResponse response = urlService.updateUrl(shortCode, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/shorten/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
        urlService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shorten/{shortCode}/stats")
    public ResponseEntity<UrlStatsResponse> getShortUrlStats(@PathVariable String shortCode) {
        UrlStatsResponse response = urlService.getUrlStats(shortCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode:[a-zA-Z0-9]{6}}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.redirect(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}
