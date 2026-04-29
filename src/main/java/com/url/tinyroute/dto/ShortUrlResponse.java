package com.url.tinyroute.dto;

import java.time.LocalDateTime;

public class ShortUrlResponse {

    private Long id;
    private String originalUrl;
    private String shortCode;
    private String shortUrl;
    private Long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean active;

    public ShortUrlResponse() {
    }

    public ShortUrlResponse(
            Long id,
            String originalUrl,
            String shortCode,
            String shortUrl,
            Long clickCount,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            Boolean active
    ) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public Boolean getActive() {
        return active;
    }
}