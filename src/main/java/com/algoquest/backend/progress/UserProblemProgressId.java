package com.algoquest.backend.progress;

import java.io.Serializable;
import java.util.Objects;

public class UserProblemProgressId implements Serializable {

    private Long userId;
    private Long problemId;

    public UserProblemProgressId() {}

    public UserProblemProgressId(Long userId, Long problemId) {
        this.userId = userId;
        this.problemId = problemId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProblemProgressId)) return false;
        UserProblemProgressId that = (UserProblemProgressId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(problemId, that.problemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, problemId);
    }
}
