package com.example.bankcards.controller;

import com.example.bankcards.entity.CardStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Component
public class StringToCardStatusConverter implements Converter<String, CardStatus> {
    @Override
    public CardStatus convert(String source) {
        if (source == null || source.isBlank()) return null;
        try {
            return CardStatus.valueOf(source.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown status: " + source);
        }
    }
}
