package com.url.tinyroute.security;

import com.url.tinyroute.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;


@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    private Key getKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("name", user.getProfileName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
   public String extractEmail(String token){
       return Jwts.parserBuilder()
               .setSigningKey(getKey())
               .build().parseClaimsJws(token)
               .getBody()
               .getSubject();
   }
   public boolean isValid (String token){
       try{
           Jwts.parserBuilder()
                   .setSigningKey(getKey())
                   .build()
                   .parseClaimsJws(token);
           return true;
       } catch (JwtException e) {
           return false;
       }
   }
}
