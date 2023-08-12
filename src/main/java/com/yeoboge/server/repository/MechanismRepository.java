package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.Mechanism;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MechanismRepository extends JpaRepository<Mechanism, Long> {
    Optional<Mechanism> findByName(String name);
}
