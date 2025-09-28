package com.example.bankcards.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TransferResponse {
    private UUID id;
    private UUID fromCardId;
    private UUID toCardId;
    private BigDecimal amount;
    private OffsetDateTime executedAt;
}
