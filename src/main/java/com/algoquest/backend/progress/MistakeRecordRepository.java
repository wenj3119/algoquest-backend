package com.algoquest.backend.progress;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MistakeRecordRepository extends JpaRepository<MistakeRecordEntity, String> {

    List<MistakeRecordEntity> findAllByUserId(Long userId);
}
