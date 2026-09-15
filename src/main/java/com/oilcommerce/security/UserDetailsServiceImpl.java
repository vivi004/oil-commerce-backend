package com.oilcommerce.security;

import com.oilcommerce.user.entity.User;
import com.oilcommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user;
        try {
            user = userRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        } catch (IllegalArgumentException e) {
            // Try by email
            user = userRepository.findByEmail(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        }

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is disabled");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getId().toString())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .accountExpired(false)
                .accountLocked(!user.isActive())
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }
}
