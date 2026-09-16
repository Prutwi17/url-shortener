package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlCreateRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.dto.UrlUpdateRequest;

public interface UrlService {
    UrlResponse createUrl(UrlCreateRequest request);
    UrlResponse getUrlByShortCode(String shortCode);
    UrlResponse updateUrl(String shortCode, UrlUpdateRequest request);
    void deleteUrl(String shortCode);
    UrlStatsResponse getUrlStats(String shortCode);
    String redirect(String shortCode);
}
