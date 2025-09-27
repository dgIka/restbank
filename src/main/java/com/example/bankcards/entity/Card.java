package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Card extends AbstractEntity{

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "pan_hash")
    private String panHash;

    @Column(name = "last4")
    private String last4;

    @Column(name = "expiry_month")
    private short expiryMonth;

    @Column(name = "expiry_year")
    private short expiryYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CardStatus status;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Override
    protected UUID getId() { return id; }

}
