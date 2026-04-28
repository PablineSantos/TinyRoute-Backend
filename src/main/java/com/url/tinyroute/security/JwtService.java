package com.url.tinyroute.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.Signature;
import java.util.Date;

@Service
public class JwtService {
   private final String SECRET_KEY = "minha-chave-super-secreta-com-mais-de-32-caracteres";

   private Key getKey(){
       return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
   }

   public String generateToken(String email){
       return Jwts.builder()
               .setSubject(email)
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis()+1000*60*60))
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
