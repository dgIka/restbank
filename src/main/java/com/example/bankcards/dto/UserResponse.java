package com.example.bankcards.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
}
