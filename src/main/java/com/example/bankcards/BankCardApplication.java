package com.example.bankcards;

import com.example.bankcards.dto.UserRegisterRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankCardApplication implements CommandLineRunner{

    private final UserService userService;
    private final CardService cardService;
    private final TransferService transferService;

    public BankCardApplication(UserService userService, CardService cardService, TransferService transferService) {
        this.userService = userService;
        this.cardService = cardService;
        this.transferService = transferService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BankCardApplication.class, args);
    }

    @Override
    public void run(String... args) {
        UserRegisterRequest req = new UserRegisterRequest();
        req.setEmail("test@example.com");
        req.setFullName("Test User");
        req.setPassword("secret123");

        UserResponse resp = null;
        try {
            resp = userService.register(req);
        } catch (Exception e) {
            System.out.println("something went wrong");
        }
        System.out.println("Создан пользователь: " + resp);
    }
}
