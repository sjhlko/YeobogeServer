package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.enums.error.UserErrorCode;
import com.yeoboge.server.handler.AppException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link User} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이스
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * {@code email}로 엔티티를 조회함.
     *
     * @param email 조회할 계정 이메일
     * @return 해당 이메일을 가진 {@link User}
     */
    Optional<User> findByEmail(final String email);

    /**
     * {@code email}의 이메일을 가진 사용자 존재 여부를 조회함.
     *
     * @param email 조회할 계정 이메일
     * @return 해당 이메일이 존재하면 true, 아니면 false
     */
    boolean existsByEmail(final String email);

    /**
     * {@code nickname}의 닉네임을 가진 사용자 존재 여부를 조회함.
     *
     * @param nickname 조회할 사용자 닉네임
     * @return 해당 닉네임이 존재하면 true, 아니면 false
     */
    boolean existsByNickname(final String nickname);

    /**
     * {@code email}로 엔티티의 {@code id}를 조회함.
     *
     * @param email 조회할 계정 이메일
     * @return 해당 이메일을 가진 엔티티의 {@code id}
     */
    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Long findIdByEmail(final String email);

    /**
     * {@code nickname} 으로 엔티티를 조회함.
     *
     * @param nickname 조회할 계정 닉네임
     * @return 해당 닉네임을 가진 {@link User}
     */
    Optional<User> findByNickname(final String nickname);

    default User getById(Long id) {
        return findById(id).orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    }

    default User getByEmail(String email) {
        return findByEmail(email).orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    }

    default User getByNickname(String nickname) {
        return findByNickname(nickname).orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    }
}
