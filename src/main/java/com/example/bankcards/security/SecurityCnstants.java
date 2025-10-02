package com.example.bankcards.security;

public final class SecurityCnstants {
    private SecurityCnstants() {}

    public static final String[] OPEN_ENDPOINTS = {
            "/api/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
}
