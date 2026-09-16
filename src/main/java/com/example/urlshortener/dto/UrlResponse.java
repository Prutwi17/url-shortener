package com.example.urlshortener.dto;
import java.time.LocalDateTime;
public record UrlResponse(Long id, String url, String shortCode, LocalDateTime createdAt, LocalDateTime updatedAt) {}
