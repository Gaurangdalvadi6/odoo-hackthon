package com.fleetflow.repository;

import com.fleetflow.entity.PasswordResetToken;
import com.fleetflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndUsedFalseAndExpiryAfter(String token, LocalDateTime now);

    Optional<PasswordResetToken> findByUserAndUsedFalseAndExpiryAfter(User user, LocalDateTime now);

    void deleteByUser(User user);
}
