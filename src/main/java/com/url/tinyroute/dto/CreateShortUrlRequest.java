package com.url.tinyroute.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public class CreateShortUrlRequest {
    @URL(message = "A URL informada não é válida (deve conter http:// ou https://)")
    @NotBlank(message = "A URL original é obrigatória")
    @Size(max = 2000, message = "A URL deve ter no máximo 2000 caracteres")
    private String originalUrl;

    @Size(min = 3, max = 50, message = "O alias deve ter entre 3 e 50 caracteres")
    private String customAlias;

    @Positive
    private Long maxClicks;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expiresAt;

    public Long getMaxClicks() {
        return maxClicks;
    }

    public void setMaxClicks(Long maxClicks) {this.maxClicks = maxClicks;}

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
