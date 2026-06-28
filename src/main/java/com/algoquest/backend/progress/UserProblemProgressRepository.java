package com.algoquest.backend.progress;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProblemProgressRepository
        extends JpaRepository<UserProblemProgressEntity, UserProblemProgressId> {

    List<UserProblemProgressEntity> findAllByUserId(Long userId);
}
