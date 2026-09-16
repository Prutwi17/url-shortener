package com.example.urlshortener.controller;

import com.example.urlshortener.dto.UrlCreateRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.dto.UrlUpdateRequest;
import com.example.urlshortener.exception.GlobalExceptionHandler;
import com.example.urlshortener.exception.ResourceNotFoundException;
import com.example.urlshortener.service.UrlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
@Import(GlobalExceptionHandler.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlService urlService;

    @Test
    void postShorten_validUrl_returns201Created() throws Exception {
        UrlResponse response = new UrlResponse(1L, "https://example.com", "abc123", LocalDateTime.now(), LocalDateTime.now());
        when(urlService.createUrl(any(UrlCreateRequest.class))).thenReturn(response);

        UrlCreateRequest request = new UrlCreateRequest("https://example.com");

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.url").value("https://example.com"))
                .andExpect(jsonPath("$.shortCode").value("abc123"));
    }

    @Test
    void postShorten_invalidUrl_returns400BadRequest() throws Exception {
        UrlCreateRequest request = new UrlCreateRequest("not-a-valid-url");

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getShorten_existingCode_returns200Ok() throws Exception {
        UrlResponse response = new UrlResponse(1L, "https://example.com", "abc123", LocalDateTime.now(), LocalDateTime.now());
        when(urlService.getUrlByShortCode("abc123")).thenReturn(response);

        mockMvc.perform(get("/shorten/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.url").value("https://example.com"));
    }

    @Test
    void getShorten_nonExistingCode_returns404NotFound() throws Exception {
        when(urlService.getUrlByShortCode("xyz789")).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/shorten/xyz789"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void putShorten_validRequest_returns200Ok() throws Exception {
        UrlResponse response = new UrlResponse(1L, "https://new-example.com", "abc123", LocalDateTime.now(), LocalDateTime.now());
        when(urlService.updateUrl(eq("abc123"), any(UrlUpdateRequest.class))).thenReturn(response);

        UrlUpdateRequest request = new UrlUpdateRequest("https://new-example.com");

        mockMvc.perform(put("/shorten/abc123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://new-example.com"));
    }

    @Test
    void putShorten_invalidUrl_returns400BadRequest() throws Exception {
        UrlUpdateRequest request = new UrlUpdateRequest("invalid-url");

        mockMvc.perform(put("/shorten/abc123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteShorten_existingCode_returns204NoContent() throws Exception {
        doNothing().when(urlService).deleteUrl("abc123");

        mockMvc.perform(delete("/shorten/abc123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShorten_nonExistingCode_returns404NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Not found")).when(urlService).deleteUrl("xyz789");

        mockMvc.perform(delete("/shorten/xyz789"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStats_existingCode_returns200Ok() throws Exception {
        UrlStatsResponse response = new UrlStatsResponse(1L, "https://example.com", "abc123", LocalDateTime.now(), LocalDateTime.now(), 5L);
        when(urlService.getUrlStats("abc123")).thenReturn(response);

        mockMvc.perform(get("/shorten/abc123/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessCount").value(5));
    }

    @Test
    void getStats_nonExistingCode_returns404NotFound() throws Exception {
        when(urlService.getUrlStats("xyz789")).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/shorten/xyz789/stats"))
                .andExpect(status().isNotFound());
    }

    @Test
    void redirect_existingCode_returns302FoundAndLocationHeader() throws Exception {
        when(urlService.redirect("abc123")).thenReturn("https://example.com");

        mockMvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://example.com"));
    }

    @Test
    void redirect_nonExistingCode_returns404NotFound() throws Exception {
        when(urlService.redirect("xyz789")).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/xyz789"))
                .andExpect(status().isNotFound());
    }
}
