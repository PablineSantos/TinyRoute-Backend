package com.url.tinyroute.service;

import com.url.tinyroute.dto.CreateShortUrlRequest;
import com.url.tinyroute.dto.ShortUrlResponse;
import com.url.tinyroute.entity.ShortUrl;
import com.url.tinyroute.entity.User;
import com.url.tinyroute.repository.ShortUrlRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    public ShortUrlService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    public ShortUrlResponse  createShortUrl(CreateShortUrlRequest createShortUrlRequest, User user) {
        validateExpirationDate(createShortUrlRequest.getExpiresAt());

        String shortCode = resolveShortCode(createShortUrlRequest.getCustomAlias());

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(createShortUrlRequest.getOriginalUrl());
        shortUrl.setShortCode(shortCode);
        shortUrl.setExpiresAt(createShortUrlRequest.getExpiresAt());
        shortUrl.setUser(user);
        shortUrlRepository.save(shortUrl);
        return toResponse(shortUrl);
    }

    public List<ShortUrlResponse> listByUser(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return shortUrlRepository.findByUserId(user.getId()).stream().map(this::toResponse).toList();
    }

    public ShortUrl findById(Long id) {
        return shortUrlRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link não encontrado"));
    }

    public ShortUrl findByShortCode(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link não encontrado"));
    }

    public String resolveOriginalUrlAndCountClick(String shortCode) {
        ShortUrl shortUrl = findByShortCode(shortCode);

        if (!shortUrl.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.GONE, "Este link não está disponível");
        }

        incrementClickCount(shortUrl);
        return shortUrl.getOriginalUrl();
    }

    public void incrementClickCount(ShortUrl shortUrl) {
        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        shortUrlRepository.save(shortUrl);
    }

    public void deleteById(Long id, User user) {

        ShortUrl shortUrl = findById(id);

        if (shortUrl.getUser() != null) {
            if (user == null || !shortUrl.getUser().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this link");
            }
        }

        shortUrlRepository.delete(shortUrl);
    }

    private void validateExpirationDate(LocalDateTime expiresAt) {
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de expiração não pode ser no passado.");
        }
    }

    private String resolveShortCode(String customAlias) {
        if (customAlias != null && !customAlias.isBlank()) {
            String normalizedAlias = customAlias.trim();
            validateShortCodeAvailability(normalizedAlias);
            return normalizedAlias;
        }

        return generateUniqueCode();
    }

    private void validateShortCodeAvailability(String shortCode) {
        if (shortUrlRepository.existsByShortCode(shortCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este apelido já esta em uso");
        }
    }

    private String generateUniqueCode() {
        String allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String generatedCode;

        do {
            StringBuilder shortCodeBuilder = new StringBuilder();

            for (int index = 0; index < 6; index++) {
                int randomPosition = random.nextInt(allowedCharacters.length());
                shortCodeBuilder.append(allowedCharacters.charAt(randomPosition));
            }

            generatedCode = shortCodeBuilder.toString();
        } while (shortUrlRepository.existsByShortCode(generatedCode));

        return generatedCode;
    }

    private ShortUrlResponse toResponse(ShortUrl shortUrl) {
        String baseUrl = "http://localhost:8080";
        String shortUrlValue = baseUrl + "/" + shortUrl.getShortCode();

        return new ShortUrlResponse(
                shortUrl.getId(),
                shortUrl.getOriginalUrl(),
                shortUrl.getShortCode(),
                shortUrlValue,
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt(),
                shortUrl.getExpiresAt(),
                shortUrl.getActive()
        );
    }
}