package com.example.bankcards.controller;

import com.example.bankcards.dto.UserRegisterRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.JwtResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Auth", description = "Регистрация и логин")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtService jwtService,
                          UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @Operation(summary = "Логин", description = "Возвращает токен")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req) {
        User user;
        try {
            user = userService.findByEmailWithRolesOrThrow(req.getEmail());
        } catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        var roles = user.getRoles().stream().map(r -> r.getName()).collect(java.util.stream.Collectors.toSet());

        String token = jwtService.generate(user.getEmail(), Map.of(
                "uid", user.getId().toString(),
                "roles", roles
        ));

        return ResponseEntity.ok(new JwtResponse(token, "Bearer", user.getEmail(), roles));
    }
    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        UserResponse user = userService.register(request);
        return ResponseEntity.ok(user);
    }
}