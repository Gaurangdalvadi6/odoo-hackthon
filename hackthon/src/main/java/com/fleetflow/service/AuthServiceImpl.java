package com.fleetflow.service;

import com.fleetflow.dto.user.ForgotPasswordRequest;
import com.fleetflow.dto.user.ForgotPasswordResponse;
import com.fleetflow.dto.user.LoginRequest;
import com.fleetflow.dto.user.MeResponse;
import com.fleetflow.dto.user.RegisterUserRequest;
import com.fleetflow.dto.user.ResetPasswordRequest;
import com.fleetflow.dto.user.UserResponse;
import com.fleetflow.entity.PasswordResetToken;
import com.fleetflow.entity.Role;
import com.fleetflow.entity.User;
import com.fleetflow.exception.CustomException;
import com.fleetflow.repository.PasswordResetTokenRepository;
import com.fleetflow.repository.RoleRepository;
import com.fleetflow.repository.UserRepository;
import com.fleetflow.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordResetTokenRepository resetTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final int TOKEN_VALIDITY_HOURS = 1;

    @Override
    @Transactional(readOnly = true)
    public MeResponse getMe(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getName())
                .distinct()
                .collect(Collectors.toList());
        String roleName = user.getRoles().stream()
                .findFirst()
                .map(r -> r.getName().name())
                .orElse(null);
        return MeResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(roleName)
                .permissions(permissions)
                .build();
    }

    @Override
    public UserResponse register(RegisterUserRequest request) {

        if(userRepo.existsByEmail(request.getEmail())){
            throw new CustomException(
                    "Email already exists",
                    HttpStatus.NOT_ACCEPTABLE
            );
        }

        Role role = roleRepo.findByName(request.getRole())
                .orElseThrow(() ->
                        new CustomException(
                                "Role not found",
                                HttpStatus.NOT_FOUND
                        ));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(role))   // ✅ FIXED
                .build();

        userRepo.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(
                        user.getRoles()
                                .stream()
                                .findFirst()
                                .map(r -> r.getName().name())
                                .orElse(null)
                )  // ✅ FIXED
                .build();
    }

    @Override
    public String login(LoginRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Invalid credentials",HttpStatus.BAD_REQUEST));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }

        return jwtService.generateToken(user);
    }

    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("No account found with this email", HttpStatus.NOT_FOUND));

        resetTokenRepo.findByUserAndUsedFalseAndExpiryAfter(user, LocalDateTime.now())
                .ifPresent(t -> resetTokenRepo.delete(t));

        String token = generateResetToken();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiry(LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS))
                .used(false)
                .build();
        resetTokenRepo.save(resetToken);

        return ForgotPasswordResponse.builder()
                .message("If an account exists, a reset link has been generated. Use the token to reset your password.")
                .resetToken(token)
                .build();
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = resetTokenRepo.findByTokenAndUsedFalseAndExpiryAfter(
                        request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new CustomException("Invalid or expired reset token", HttpStatus.BAD_REQUEST));

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        resetToken.setUsed(true);
        resetTokenRepo.save(resetToken);

        return jwtService.generateToken(user);
    }

    private String generateResetToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
