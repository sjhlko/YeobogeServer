package com.yeoboge.server.service.impl;

import com.yeoboge.server.entity.CustomUserDetails;
import com.yeoboge.server.entity.User;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Username %s Not Found", username)
                ));
        return CustomUserDetails.build(user);
    }
}
