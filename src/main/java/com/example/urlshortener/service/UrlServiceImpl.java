package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlCreateRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.dto.UrlUpdateRequest;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.exception.ResourceNotFoundException;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlServiceImpl(UrlRepository urlRepository, ShortCodeGenerator shortCodeGenerator) {
        this.urlRepository = urlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    @Override
    @Transactional
    public UrlResponse createUrl(UrlCreateRequest request) {
        String shortCode = generateUniqueShortCode();
        Url url = new Url();
        url.setUrl(request.url());
        url.setShortCode(shortCode);
        Url savedUrl = urlRepository.save(url);
        return mapToResponse(savedUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public UrlResponse getUrlByShortCode(String shortCode) {
        Url url = findEntityByShortCode(shortCode);
        return mapToResponse(url);
    }

    @Override
    @Transactional
    public UrlResponse updateUrl(String shortCode, UrlUpdateRequest request) {
        Url url = findEntityByShortCode(shortCode);
        url.setUrl(request.url());
        Url updatedUrl = urlRepository.save(url);
        return mapToResponse(updatedUrl);
    }

    @Override
    @Transactional
    public void deleteUrl(String shortCode) {
        Url url = findEntityByShortCode(shortCode);
        urlRepository.delete(url);
    }

    @Override
    @Transactional(readOnly = true)
    public UrlStatsResponse getUrlStats(String shortCode) {
        Url url = findEntityByShortCode(shortCode);
        return mapToStatsResponse(url);
    }

    @Override
    @Transactional
    public String redirect(String shortCode) {
        Url url = findEntityByShortCode(shortCode);
        url.incrementAccessCount();
        urlRepository.save(url);
        return url.getUrl();
    }

    private Url findEntityByShortCode(String shortCode) {
        String cleanCode = shortCode != null ? shortCode.trim() : "";
        return urlRepository.findByShortCode(cleanCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found for code: " + cleanCode));
    }

    private String generateUniqueShortCode() {
        int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            String code = shortCodeGenerator.generate();
            if (!urlRepository.existsByShortCode(code)) {
                return code;
            }
        }
        throw new RuntimeException("Failed to generate unique short code after maximum attempts");
    }

    private UrlResponse mapToResponse(Url url) {
        return new UrlResponse(
                url.getId(),
                url.getUrl(),
                url.getShortCode(),
                url.getCreatedAt(),
                url.getUpdatedAt()
        );
    }

    private UrlStatsResponse mapToStatsResponse(Url url) {
        return new UrlStatsResponse(
                url.getId(),
                url.getUrl(),
                url.getShortCode(),
                url.getCreatedAt(),
                url.getUpdatedAt(),
                url.getAccessCount()
        );
    }
}
