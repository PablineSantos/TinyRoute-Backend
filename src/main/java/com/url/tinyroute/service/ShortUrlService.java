package com.url.tinyroute.service;

import com.url.tinyroute.dto.CreateShortUrlRequest;
import com.url.tinyroute.dto.ShortUrlResponse;
import com.url.tinyroute.entity.ShortUrl;
import com.url.tinyroute.entity.User;
import com.url.tinyroute.exception.BusinessException;
import com.url.tinyroute.exception.DataConflictException;
import com.url.tinyroute.exception.ExpiredUrlException;
import com.url.tinyroute.exception.ResourceNotFoundException;
import com.url.tinyroute.repository.ShortUrlRepository;
import com.url.tinyroute.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    @Value("${app.base-url:http://localhost:8080/api/urls/r}")
    private String baseUrl;

    public ShortUrlService(ShortUrlRepository shortUrlRepository, UserRepository userRepository) {
        this.shortUrlRepository = shortUrlRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ShortUrlResponse createShortUrl(CreateShortUrlRequest request, Authentication authentication) {

        validateMaxClicks(request.getMaxClicks());
        User user = extractUser(authentication);
        validateExpirationDate(request.getExpiresAt());

        if (user == null && (request.getMaxClicks() != null || request.getExpiresAt() != null)) {
            throw new BusinessException("Você precisa estar logado para usar limites de expiração", HttpStatus.FORBIDDEN);
        }
        String shortCode = resolveShortCode(request.getCustomAlias());

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(request.getOriginalUrl());
        shortUrl.setShortCode(shortCode);
        shortUrl.setExpiresAt(request.getExpiresAt());
        shortUrl.setMaxClicks(request.getMaxClicks());
        shortUrl.setUser(user);

        shortUrlRepository.save(shortUrl);
        return toResponse(shortUrl);
    }

    @Transactional(readOnly = true)
    public List<ShortUrlResponse> listByUser(User user,String alias) {
        if (user == null) {
            throw new BusinessException("Usuário não autenticado", HttpStatus.UNAUTHORIZED);
        }
        List<ShortUrl> urls;

        if (alias != null && !alias.isBlank()) {
            urls = shortUrlRepository.findByUserIdAndShortCodeContainingIgnoreCase(user.getId(), alias);
        } else {
            urls = shortUrlRepository.findByUserId(user.getId());
        }

        return urls.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ShortUrl findById(Long id) {
        return shortUrlRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Link não encontrado"));
    }

    @Transactional(readOnly = true)
    public ShortUrl findByShortCode(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode).orElseThrow(() -> new ResourceNotFoundException("Link não encontrado"));
    }

    @Transactional
    public String resolveOriginalUrlAndCountClick(String shortCode) {
        ShortUrl shortUrl = findByShortCode(shortCode);

        if (!shortUrl.isAvailable()) {
            throw new ExpiredUrlException("Este link atingiu o limite de cliques ou expirou.");
        }

        incrementClickCount(shortUrl);

        if (shortUrl.hasReachedClickLimit()) {
            shortUrl.setActive(false);
        }

        return shortUrl.getOriginalUrl();
    }

    private void incrementClickCount(ShortUrl shortUrl) {
        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
    }

    @Transactional
    public void deleteById(Long id, User user) {

        if (user == null) {
            throw new BusinessException("Usuário não autenticado", HttpStatus.UNAUTHORIZED);
        }

        User freshUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.UNAUTHORIZED));
        ShortUrl shortUrl = findById(id);

        if (shortUrl.getUser() == null ||
                !shortUrl.getUser().getId().equals(freshUser.getId())) {

            throw new BusinessException("Você não pode deletar este link", HttpStatus.FORBIDDEN);
        }

        shortUrlRepository.delete(shortUrl);
    }
    private void validateExpirationDate(LocalDateTime expiresAt) {
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            throw new BusinessException("A data de expiração não pode ser no passado.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateMaxClicks(Long maxClicks) {
        if (maxClicks != null && maxClicks <= 0) {
            throw new BusinessException("O limite de cliques deve ser maior que zero.", HttpStatus.BAD_REQUEST);
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
            throw new DataConflictException("Este apelido já esta em uso");
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

    private User extractUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }

        return (User) authentication.getPrincipal();
    }

    private ShortUrlResponse toResponse(ShortUrl shortUrl) {
        String shortUrlValue = baseUrl + "/" + shortUrl.getShortCode();

        return new ShortUrlResponse(
                shortUrl.getId(),
                shortUrl.getOriginalUrl(),
                shortUrl.getShortCode(),
                shortUrlValue,
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt(),
                shortUrl.getExpiresAt(),
                shortUrl.getActive(),
                shortUrl.getMaxClicks()
        );
    }
}