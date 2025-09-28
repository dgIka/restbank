package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CardResponse {
    private UUID id;
    private String maskedPan;
    private CardStatus status;
    private BigDecimal balance;
    private int expiryMonth;
    private int expiryYear;
    private OffsetDateTime createdAt;
}
