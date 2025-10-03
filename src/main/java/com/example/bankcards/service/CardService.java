package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.PanCrypto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService{
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final PanCrypto panCrypto;
    private final EntityManager entityManager;

    public CardResponse createForUser(UUID userId, CardCreateRequest cardCreateRequest) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        YearMonth exp = YearMonth.of(cardCreateRequest.getExpiryYear(), cardCreateRequest.getExpiryMonth());
        if (exp.isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("Card expired");
        }

        String hash = panCrypto.hash(cardCreateRequest.getPan());
        if (cardRepository.existsByPanHash(hash)) {
            throw new IllegalArgumentException("Card already exists");
        }

        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setUser(owner);
        card.setPanHash(hash);
        card.setLast4(panCrypto.last4(cardCreateRequest.getPan()));
        card.setExpiryMonth((short) cardCreateRequest.getExpiryMonth());
        card.setExpiryYear((short) cardCreateRequest.getExpiryYear());
        card.setStatus(CardStatus.ACTIVE);
        OffsetDateTime now = OffsetDateTime.now();
        card.setCreatedAt(now);
        card.setUpdatedAt(now);

        cardRepository.save(card);
        return toResponse(card);
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> pageMyCards(UUID userId, CardStatus cardStatus, Pageable pageable) {
        Page<Card> page = (cardStatus == null)
                ? cardRepository.findAllByUserId(userId, pageable)
                : cardRepository.findAllByUserIdAndStatus(userId, cardStatus, pageable);
        return page.map(this::toResponse);
    }

    @Transactional
    public void block(UUID cardId) {
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        card.setStatus(CardStatus.BLOCKED);
        card.setUpdatedAt(OffsetDateTime.now());
    }

    @Transactional
    public void activate(UUID cardId) {
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        card.setStatus(CardStatus.ACTIVE);
        card.setUpdatedAt(OffsetDateTime.now());
    }

    @Transactional
    public void delete(UUID cardId) {
        cardRepository.deleteById(cardId);
    }

    @Transactional
    public Card lockCardForUpdate(UUID cardId) {
        Card ref = entityManager.find(Card.class, cardId, LockModeType.PESSIMISTIC_WRITE);
        if (ref == null) throw new IllegalArgumentException("Card not found: " + cardId);
        return ref;
    }



    private CardResponse toResponse(Card c) {
        CardResponse cardResponse = new CardResponse();
        cardResponse.setId(c.getId());
        cardResponse.setMaskedPan(panCrypto.maskFromLast4(c.getLast4()));
        cardResponse.setStatus(c.getStatus());
        cardResponse.setBalance(c.getBalance());
        cardResponse.setExpiryMonth(c.getExpiryMonth());
        cardResponse.setExpiryYear(c.getExpiryYear());
        cardResponse.setCreatedAt(c.getCreatedAt());
        return cardResponse;
        

    }
}
