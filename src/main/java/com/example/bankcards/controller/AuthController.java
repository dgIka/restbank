package com.example.bankcards.controller;

import com.example.bankcards.dto.UserRegisterRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.AuthService;
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
    private final AuthService authService;

    public AuthController(JwtService jwtService,
                          UserService userService,
                          PasswordEncoder passwordEncoder, AuthService authService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.authService = authService;
    }
    @Operation(summary = "Логин", description = "Возвращает токен")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req) {
        User user = userService.findByEmailWithRolesOrThrow(req.getEmail());

        JwtResponse jwt = authService.login(req, user, jwtService);

        return ResponseEntity.ok(jwt);
    }
    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        UserResponse user = userService.register(request);
        return ResponseEntity.ok(user);
    }
}