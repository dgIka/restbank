package com.example.bankcards.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferRequest {
    @NotNull
    private UUID fromCardId;

    @NotNull
    private UUID toCardId;

    @Positive
    @Digits(integer = 19, fraction = 2)
    private BigDecimal amount;
}
