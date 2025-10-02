package com.example.bankcards;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.UserRegisterRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.RoleName;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.UUID;

@SpringBootApplication
public class BankCardApplication implements CommandLineRunner{

    private final UserRepository userRepository;
    private final UserService userService;
    private final CardService cardService;
    private final TransferService transferService;
    private final CardRepository cardRepository;

    public BankCardApplication(UserService userService, CardService cardService
            , TransferService transferService
            , CardRepository cardRepository, UserRepository userRepository) {
        this.userService = userService;
        this.cardService = cardService;
        this.transferService = transferService;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;

    }

    public static void main(String[] args) {
        SpringApplication.run(BankCardApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("=== DEMO SMOKE START ===");



        // 1) register user (идемпотентно по email)
        String email = "demo.user@example.com";
        UserResponse userResp;
        try {
            var reg = new UserRegisterRequest();
            reg.setEmail(email);
            reg.setFullName("Demo User");
            reg.setPassword("password1234");
            userResp = userService.register(reg);
            System.out.println("Создан пользователь: " + userResp);
        } catch (Exception e) {
            // если такой email уже есть — просто возьмём существующего
            var existing = userRepository.findByEmail(email).orElseThrow();
            userResp = new UserResponse();
            userResp.setId(existing.getId());
            userResp.setEmail(existing.getEmail());
            userResp.setFullName(existing.getFullName());
            System.out.println("Пользователь уже существует: " + userResp);
        }
        UUID userId = userResp.getId();

        // 2) create two cards
        YearMonth exp = YearMonth.now().plusMonths(6);

        var cReq1 = new CardCreateRequest();
        cReq1.setPan("4111111111111111");
        cReq1.setExpiryMonth(exp.getMonthValue());
        cReq1.setExpiryYear(exp.getYear());

        var cReq2 = new CardCreateRequest();
        cReq2.setPan("5555555555554444");
        cReq2.setExpiryMonth(exp.getMonthValue());
        cReq2.setExpiryYear(exp.getYear());

        var card1 = cardService.createForUser(userId, cReq1);
        var card2 = cardService.createForUser(userId, cReq2);

        System.out.println("Карта 1: " + card1);
        System.out.println("Карта 2: " + card2);

        // 3) top-up balance of card1 manually for demo (имитация пополнения)
        Card from = cardRepository.findById(card1.getId()).orElseThrow();
        from.setBalance(new BigDecimal("100.00"));
        from.setUpdatedAt(OffsetDateTime.now());
        // save не обязателен, если в одной транзакции; но тут явно фиксируем
        cardRepository.save(from);

        // 4) transfer 30.00 from card1 to card2 (плюс идемпотентность)
        var tReq = new TransferRequest();
        tReq.setFromCardId(card1.getId());
        tReq.setToCardId(card2.getId());
        tReq.setAmount(new BigDecimal("30.00"));

        String idem = "demo-idem-1";
        var t1 = transferService.transfer(userId, tReq, idem);
        var t2 = transferService.transfer(userId, tReq, idem); // тот же ключ → вернёт тот же перевод

        System.out.println("Перевод #1: " + t1);
        System.out.println("Перевод #2 (идемпотентность): " + t2);

        // 5) check balances after transfer
        var fromNow = cardRepository.findById(card1.getId()).orElseThrow();
        var toNow = cardRepository.findById(card2.getId()).orElseThrow();
        System.out.println("Баланс отправителя: " + fromNow.getBalance()); // 70.00
        System.out.println("Баланс получателя: " + toNow.getBalance());    // 30.00

        // 6) pagination demo (мои карты)
        var page = cardService.pageMyCards(userId, null, PageRequest.of(0, 10));
        System.out.println("Всего карт у пользователя: " + page.getTotalElements());
        page.getContent().forEach(c -> System.out.println("   - " + c.getId() + " " + c.getMaskedPan()));

        System.out.println("=== DEMO SMOKE DONE ===");
    }
}
