package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class PanCrypto {
    private final String pepper;

    public PanCrypto(@Value("${security.pan.pepper:}")String pepper) {
        this.pepper = pepper == null ? "" : pepper;
    }

    public String hash(String pan) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest((pan + pepper).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("PAN hash error", e);
        }
    }

    public String last4(String pan) {
        return pan.substring(pan.length() - 4);
    }

    public String maskFromLast4(String last4) {
        return "**** **** **** " + last4;
    }

}
