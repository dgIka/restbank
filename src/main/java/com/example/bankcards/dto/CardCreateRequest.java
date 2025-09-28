package com.example.bankcards.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CardCreateRequest {

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "PAN должен содержать ровно 16 цифр без пробелов")
    private String pan;

    @Min(1) @Max(12)
    private int expiryMonth;

    @Min(2000) @Max(2100)
    private int expiryYear;
}
