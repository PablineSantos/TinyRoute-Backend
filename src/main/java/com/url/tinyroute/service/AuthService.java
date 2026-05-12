package com.url.tinyroute.service;

import com.url.tinyroute.dto.LoginRequest;
import com.url.tinyroute.dto.RegisterRequest;
import com.url.tinyroute.entity.User;
import com.url.tinyroute.exception.DataConflictException;
import com.url.tinyroute.exception.InvalidCredentialsException;
import com.url.tinyroute.exception.ResourceNotFoundException;
import com.url.tinyroute.repository.UserRepository;
import com.url.tinyroute.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataConflictException("Email já existe");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Usuário não foi encontrado"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }

        return jwtService.generateToken(user);
    }
}
