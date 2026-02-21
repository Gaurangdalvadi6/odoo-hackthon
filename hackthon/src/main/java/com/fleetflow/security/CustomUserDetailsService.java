package com.fleetflow.security;

import com.fleetflow.entity.User;
import com.fleetflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add roles
        user.getRoles().forEach(role -> {
            authorities.add(
                    new SimpleGrantedAuthority(role.getName().name())
            );

            // Add permissions
            role.getPermissions().forEach(permission ->
                    authorities.add(
                            new SimpleGrantedAuthority(permission.getName())
                    )
            );
        });

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}