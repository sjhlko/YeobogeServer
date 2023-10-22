package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.embeddable.RecommendationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

/**
 * 최근 그룹 추천 결과와 관련해 {@link RecommendationHistory}에 대한 DB 쿼리 메서드를 제공하는 인터페이스
 */
public interface RecommendationHistoryRepository extends JpaRepository<RecommendationHistory, Long> {
    @Transactional
    @Modifying
    void deleteAllByUserId(long userId);
}
