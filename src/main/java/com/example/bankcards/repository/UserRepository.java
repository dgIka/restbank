package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String Email);
    boolean existsByEmail(String email);
    @Query("""
       select u
       from User u
       left join fetch u.roles r
       where u.email = :email
       """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);


}
