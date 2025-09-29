package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferService transferService;
    private final CardRepository cardRepository;
    private final CardService cardService;
    private final TransferRepository transferRepository;

    @Transactional
    public TransferResponse transfer(UUID userId, TransferRequest transferRequest, String idempotencyKey) {
        if (transferRequest.getFromCardId().equals(transferRequest.getToCardId())) {
            throw new IllegalArgumentException("from and to are the same cards");
        }

        if(idempotencyKey != null && !idempotencyKey.isEmpty()) {
            Optional<Transfer> ts = transferRepository.findByIdempotencyKey(idempotencyKey);
            if (ts.isPresent()) {
                return toResponse(ts.get());
            }
        }

        UUID a = transferRequest.getFromCardId(), b = transferRequest.getToCardId();
        UUID first = Comparator.<UUID>naturalOrder().compare(a, b) <= 0 ? a : b;
        UUID second = first.equals(a) ? b : a;

        Card firstLocked = cardService.lockCardForUpdate(first);
        Card secondLocked = cardService.lockCardForUpdate(second);

        Card from = firstLocked.getId().equals(transferRequest.getFromCardId()) ? firstLocked : secondLocked;
        Card to   = from == firstLocked ? secondLocked : firstLocked;

        if (userId != null) {
            if (!from.getUser().getId().equals(userId) || !to.getUser().getId().equals(userId)) {
                throw new SecurityException("Cards must belong to the user");
            }
        }

        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Both cards must be ACTIVE");
        }


        BigDecimal amount = transferRequest.getAmount();
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }


        OffsetDateTime now = OffsetDateTime.now();
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        from.setUpdatedAt(now);
        to.setUpdatedAt(now);


        Transfer transfer = Transfer.builder()
                .id(UUID.randomUUID())
                .fromCard(from)
                .toCard(to)
                .amount(amount)
                .idempotencyKey(idempotencyKey)
                .createdAt(now)
                .updatedAt(now)
                .executedAt(now)
                .build();

        transferRepository.save(transfer);
        return toResponse(transfer);



    }

    private TransferResponse toResponse(Transfer t) {
        var dto = new TransferResponse();
        dto.setId(t.getId());
        dto.setFromCardId(t.getFromCard().getId());
        dto.setToCardId(t.getToCard().getId());
        dto.setAmount(t.getAmount());
        dto.setExecutedAt(t.getExecutedAt());
        return dto;
    }

}
