package com.algoquest.backend.db.repository;

import com.algoquest.backend.db.entity.ProblemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {
    List<ProblemEntity> findAllByOrderBySortOrderAscIdAsc();
}
