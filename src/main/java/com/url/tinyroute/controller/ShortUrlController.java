package com.url.tinyroute.controller;

import com.url.tinyroute.dto.CreateShortUrlRequest;
import com.url.tinyroute.dto.ShortUrlResponse;
import com.url.tinyroute.entity.ShortUrl;
import com.url.tinyroute.entity.User;
import com.url.tinyroute.service.ShortUrlService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/urls")
@SecurityRequirement(name = "bearerAuth")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse> create(@Valid @RequestBody CreateShortUrlRequest request, Authentication authentication) {
        ShortUrlResponse response = shortUrlService.createShortUrl(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ShortUrlResponse>> listMyUrls(Authentication authentication,@RequestParam(required = false) String alias) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(shortUrlService.listByUser(user, alias));
    }

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = shortUrlService.resolveOriginalUrlAndCountClick(shortCode);

        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        shortUrlService.deleteById(id, user);
        return ResponseEntity.noContent().build();
    }
}
