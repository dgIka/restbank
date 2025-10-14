package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.CurrentUserService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.UUID;

@Tag(name = "Card", description = "Взаимодействие с картами")
@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;
    private final UserService userService;
    private final CurrentUserService currentUserService;

    public CardController(CardService cardService, UserService userService, CurrentUserService currentUserService) {
        this.cardService = cardService;
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @Operation(summary = "Выпуск новой карты")
    @PostMapping("/issue/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CardResponse issueForUser(@PathVariable UUID userId,
                                     @RequestBody @Valid CardCreateRequest req) {
        return cardService.createForUser(userId, req);
    }

    @Operation(summary = "Пагинация")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<CardResponse> pageMyCards(@RequestParam(required = false) CardStatus status,
                                          Pageable pageable) {
        UUID userId = currentUserService.getCurrentUserId();

        return cardService.pageMyCards(userId, status, pageable);
    }

    @Operation(summary = "Блокировка карты")
    @PostMapping("/{cardId}/block")
    @PreAuthorize("hasRole('USER')")
    public void blockMyCard(@PathVariable UUID cardId) {
        cardService.block(cardId);
    }

    @Operation(summary = "Активация карты")
    @PostMapping("/{cardId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public void activate(@PathVariable UUID cardId) {
        cardService.activate(cardId);
    }

    @Operation(summary = "Удаление карты")
    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID cardId) {
        cardService.delete(cardId);
    }


}
