package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.auth.UserDetailsDto;
import com.yeoboge.server.domain.entity.CustomUserDetails;
import com.yeoboge.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security의 {@link UserDetailsService}의 커스텀 서비스
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * 계정 이메일로 DB의 사용자를 조회하여 반환함.
     * @param username the username identifying the user whose data is required.
     * @return {@link CustomUserDetails}
     * @throws UsernameNotFoundException 사용자가 DB에 존재하지 않을 때 던짐
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailsDto userDto = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Username %s Not Found", username)
                ));
        return CustomUserDetails.build(userDto);
    }
}
