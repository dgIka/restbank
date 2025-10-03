package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
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

    public CardController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
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
    public Page<CardResponse> pageMyCards(@RequestParam(required = false) String status,
                                          Pageable pageable) {
        UUID userId = currentUserId();
        CardStatus st = null;
        if (status != null && !status.isBlank()) {
            try {
                st = CardStatus.valueOf(status.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown status: " + status);
            }
        }
        return cardService.pageMyCards(userId, st, pageable);
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

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authentication");
        }
        return userService.findByEmailOrThrow(auth.getName()).getId();
    }
}
