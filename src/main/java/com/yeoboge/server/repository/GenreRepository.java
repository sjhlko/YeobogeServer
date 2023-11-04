package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link Genre} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이스
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findById(Long id);
}
