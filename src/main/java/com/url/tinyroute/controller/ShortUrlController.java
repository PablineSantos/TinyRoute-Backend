package com.url.tinyroute.controller;

import com.url.tinyroute.dto.CreateShortUrlRequest;
import com.url.tinyroute.dto.ShortUrlResponse;
import com.url.tinyroute.entity.ShortUrl;
import com.url.tinyroute.entity.User;
import com.url.tinyroute.service.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/urls")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;


    public ShortUrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse>create(@Valid @RequestBody CreateShortUrlRequest createShortUrlRequest, HttpServletRequest httpRequest){
        User user = (User) httpRequest.getAttribute("user");
        ShortUrlResponse shortUrlResponse=shortUrlService.createShortUrl(createShortUrlRequest, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(shortUrlResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ShortUrlResponse>> listMyUrls(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println("USER ID: " + user.getId());
        System.out.println("USER EMAIL: " + user.getEmail());
        return ResponseEntity.ok(shortUrlService.listByUser(user));
    }

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode){
        String originalUrl = shortUrlService.resolveOriginalUrlAndCountClick(shortCode);

        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id,
                                           HttpServletRequest request) {

        User user = (User) request.getAttribute("user");

        shortUrlService.deleteById(id, user);

        return ResponseEntity.noContent().build();
    }
}
