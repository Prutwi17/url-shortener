package com.example.urlshortener.dto;

import com.example.urlshortener.validation.ValidUrl;
import jakarta.validation.constraints.NotBlank;

public record UrlUpdateRequest(
    @NotBlank(message = "URL must not be blank")
    @ValidUrl(message = "URL must be a valid HTTP or HTTPS URL")
    String url
) {}
