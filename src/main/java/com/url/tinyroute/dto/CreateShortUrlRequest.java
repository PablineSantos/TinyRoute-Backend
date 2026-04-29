package com.url.tinyroute.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CreateShortUrlRequest {
    @NotBlank(message = "A URL original é obrigatória")
    @Size(max=2000, message ="A URL deve ter no máximo 2000 caracteres" )
    private String originalUrl;

    @Size(min = 3, max = 50, message = "O alias deve ter entre 3 e 50 caracteres")
    private String customAlias;

    private LocalDateTime expiresAt;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
