package com.example.urlshortener.dto;
import java.time.LocalDateTime;
public record UrlStatsResponse(Long id, String url, String shortCode, LocalDateTime createdAt, LocalDateTime updatedAt, Long accessCount) {}
