package com.url.tinyroute.security;

import com.url.tinyroute.entity.User;
import com.url.tinyroute.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/api/urls/r/")) {
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("AUTH HEADER: " + request.getHeader("Authorization"));
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            if (jwtService.isValid(token)) {

                String email = jwtService.extractEmail(token);

                User user = userRepository.findByEmail(email).orElseThrow();

                if (user != null) {

                    var authentication =
                            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    java.util.Collections.emptyList()
                            );

                    org.springframework.security.core.context.SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }


        }


        filterChain.doFilter(request, response);
    }
}

