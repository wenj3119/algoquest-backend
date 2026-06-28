package com.algoquest.backend.db.repository;

import com.algoquest.backend.db.entity.ProblemStepOptionEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemStepOptionRepository extends JpaRepository<ProblemStepOptionEntity, Long> {
    List<ProblemStepOptionEntity> findByStepIdInOrderBySortOrder(Collection<Long> stepIds);
}
