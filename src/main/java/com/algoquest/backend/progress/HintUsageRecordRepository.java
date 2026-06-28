package com.algoquest.backend.progress;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HintUsageRecordRepository extends JpaRepository<HintUsageRecordEntity, String> {

    List<HintUsageRecordEntity> findAllByUserId(Long userId);
}
