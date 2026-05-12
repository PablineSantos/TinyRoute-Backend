package com.url.tinyroute.repository;

import com.url.tinyroute.entity.ShortUrl;
import com.url.tinyroute.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl,Long> {
    Optional<ShortUrl> findByShortCode(String shortCode);
    List<ShortUrl> findByUser(User user);
    boolean existsByShortCode(String shortCode);

    List<ShortUrl> findByUserId(Long userId);

    List<ShortUrl> findByUserIdAndShortCodeContainingIgnoreCase(Long userId, String shortCode);
}
