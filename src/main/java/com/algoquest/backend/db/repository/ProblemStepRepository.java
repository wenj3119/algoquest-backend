package com.algoquest.backend.db.repository;

import com.algoquest.backend.db.entity.ProblemStepEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemStepRepository extends JpaRepository<ProblemStepEntity, Long> {
    List<ProblemStepEntity> findByProblemIdOrderBySortOrder(Long problemId);
}
