package com.example.bankcards.service;

import com.example.bankcards.dto.auth.JwtResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public JwtResponse login(LoginRequest req, User user, JwtService jwtService) {
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        var roles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());

        String token = jwtService.generate(user.getEmail(), Map.of(
                "uid", user.getId().toString(),
                "roles", roles
        ));

        JwtResponse jwt = new JwtResponse(token, "Bearer", user.getEmail(), roles);

        return jwt;

    }
}
