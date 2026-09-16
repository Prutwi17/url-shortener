package com.example.urlshortener;

import com.example.urlshortener.dto.UrlCreateRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UrlShortenerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testEndToEndUrlShortenerLifecycle() throws Exception {
        // 1. Create short URL
        UrlCreateRequest createRequest = new UrlCreateRequest("https://spring.io");
        MvcResult createResult = mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").exists())
                .andExpect(jsonPath("$.url").value("https://spring.io"))
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        UrlResponse createdUrl = objectMapper.readValue(responseJson, UrlResponse.class);
        String shortCode = createdUrl.shortCode();
        assertNotNull(shortCode);
        assertEquals(6, shortCode.length());

        // 2. Retrieve URL information
        mockMvc.perform(get("/shorten/" + shortCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://spring.io"))
                .andExpect(jsonPath("$.shortCode").value(shortCode));

        // 3. Initial stats check (accessCount should be 0)
        mockMvc.perform(get("/shorten/" + shortCode + "/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessCount").value(0));

        // 4. Redirect (accessCount increments by 1)
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://spring.io"));

        // 5. Verify stats check (accessCount should be 1)
        mockMvc.perform(get("/shorten/" + shortCode + "/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessCount").value(1));

        // 6. Update URL
        UrlUpdateRequest updateRequest = new UrlUpdateRequest("https://spring.io/projects/spring-boot");
        mockMvc.perform(put("/shorten/" + shortCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://spring.io/projects/spring-boot"))
                .andExpect(jsonPath("$.shortCode").value(shortCode))
                .andExpect(jsonPath("$.id").value(createdUrl.id()));

        // 7. Verify redirect after update uses new URL
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://spring.io/projects/spring-boot"));

        // 8. Verify stats accessCount is now 2
        mockMvc.perform(get("/shorten/" + shortCode + "/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessCount").value(2));

        // 9. Delete URL
        mockMvc.perform(delete("/shorten/" + shortCode))
                .andExpect(status().isNoContent());

        // 10. Verify 404 after deletion
        mockMvc.perform(get("/shorten/" + shortCode))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isNotFound());
    }

    @Test
    void testStaticFrontendServed() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("URL Shortener")));
    }
}
