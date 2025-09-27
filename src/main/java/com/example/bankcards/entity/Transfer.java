package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Transfer extends AbstractEntity<UUID> {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_id")
    private Card fromCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_id")
    private Card toCard;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "executed_at")
    private OffsetDateTime executedAt;

    @Override
    protected UUID getId() { return id; }
}
