package com.example.bankcards.dto.auth;

import com.example.bankcards.entity.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class JwtResponse {

    private String token;
    private String tokenType;
    private String email;
    private Set<RoleName> roles;
}
