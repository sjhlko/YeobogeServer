package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.auth.UserDetailsDto;
import com.yeoboge.server.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<UserDetailsDto> findByEmail(String email);
    Optional<User> findUserByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Long findIdByEmail(String email);
}
