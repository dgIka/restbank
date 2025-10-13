package com.example.bankcards.service;

import com.example.bankcards.dto.UserRegisterRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.RoleName;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        var user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        Role role = roleRepository.findByName(RoleName.USER) // или findByName("USER") — как у тебя реализовано
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role USER not found"));

        user.getRoles().add(role);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        var resp = new UserResponse();
        resp.setId(user.getId());
        resp.setEmail(user.getEmail());
        resp.setFullName(user.getFullName());
        return resp;
    }

    @Transactional(readOnly = true)
    public User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by email"));
    }

    @Transactional(readOnly = true)
    public User findByEmailWithRolesOrThrow(String email) {
        return userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }
}