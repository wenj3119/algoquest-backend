package com.algoquest.backend.db.repository;

import com.algoquest.backend.db.entity.TestCaseEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Long> {
    List<TestCaseEntity> findByProblemIdOrderBySortOrder(Long problemId);
}
