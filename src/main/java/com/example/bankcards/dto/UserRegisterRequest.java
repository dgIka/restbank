package com.example.bankcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @Email(message = "Incorrect email")
    @NotBlank
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(min = 1, max = 255)
    private String fullName;

    @NotBlank
    @Size(min = 8, max = 72, message = "Password should be 8-72 symbols")
    private String password;
}
