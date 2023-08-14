package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.Rating;
import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link Rating} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long>, CustomRatingRepository {
    /**
     * 회원이 특정 보드게임에 대해 평가한 정보를 조회함.
     *
     * @param userId 조회할 회원 ID
     * @param boardGameId 평가한 보드게임 ID
     * @return {@link Rating}의 {@link Optional} 객체
     */
    @Query("SELECT r FROM Rating r WHERE r.user.id = :userId and r.boardGame.id = :boardGameId")
    Optional<Rating> findByParentId(Long userId, Long boardGameId);

    /**
     * 보드게임을 평가한 데이터가 있다면 해당 엔티티를 반환하고,
     * 없다면 새 객체를 생성해 반환함.
     *
     * @param userId 조회할 회원 ID
     * @param boardGameId 평가한 보드게임 ID
     * @return {@link Rating}
     */
    default Rating getOrNewByParentId(Long userId, Long boardGameId) {
        return findByParentId(userId, boardGameId).orElse(new Rating());
    }

    /**
     * 보드게임을 평가한 데이터에 대한 엔티티를 반환함.
     *
     * @param userId 조회할 회원 ID
     * @param boardGameId 평가한 보드게임 ID
     * @return {@link Rating}
     * @throws AppException 회원이 해당 보드게임을 평가한 적 없다면, 404 응답인 {@code NOT_FOUND} 예외를 던짐.
     */
    default Rating getByParentId(Long userId, Long boardGameId) {
        return findByParentId(userId, boardGameId)
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND));
    }
}
