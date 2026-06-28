package com.algoquest.backend.db.repository;

import com.algoquest.backend.db.entity.ProblemExampleEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemExampleRepository extends JpaRepository<ProblemExampleEntity, Long> {
    List<ProblemExampleEntity> findByProblemIdOrderBySortOrder(Long problemId);
}
