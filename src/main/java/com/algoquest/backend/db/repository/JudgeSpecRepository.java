package com.algoquest.backend.db.repository;

import com.algoquest.backend.db.entity.JudgeSpecEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JudgeSpecRepository extends JpaRepository<JudgeSpecEntity, Long> {
    Optional<JudgeSpecEntity> findByProblemId(Long problemId);
    boolean existsByProblemId(Long problemId);
}
