package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.auth.UserDetailsDto;
import com.yeoboge.server.domain.entity.CustomUserDetails;
import com.yeoboge.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailsDto userDto = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Username %s Not Found", username)
                ));
        return CustomUserDetails.build(userDto);
    }
}
