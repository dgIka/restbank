package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    Page<Card> findById(UUID Id, Pageable p);
    Page<Card> findByIdAndStatus(UUID Id, CardStatus status, Pageable p);

    Optional<Card> findByPanHash(String panHash);

    boolean existsByPanHash(String panHash);

}
