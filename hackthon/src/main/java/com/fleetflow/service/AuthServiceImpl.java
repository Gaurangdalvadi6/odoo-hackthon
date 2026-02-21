package com.fleetflow.service;

import com.fleetflow.dto.user.LoginRequest;
import com.fleetflow.dto.user.RegisterUserRequest;
import com.fleetflow.dto.user.UserResponse;
import com.fleetflow.entity.Role;
import com.fleetflow.entity.User;
import com.fleetflow.repository.RoleRepository;
import com.fleetflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserResponse register(RegisterUserRequest request) {

        if(userRepo.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepo.findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepo.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().getName().name())
                .build();
    }

    @Override
    public String login(LoginRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }

        return jwtService.generateToken(user);
    }
}
