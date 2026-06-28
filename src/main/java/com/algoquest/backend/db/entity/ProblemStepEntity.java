package com.algoquest.backend.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "problem_steps")
public class ProblemStepEntity {

    @Id
    private Long id;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "step_key", nullable = false)
    private String stepKey;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(name = "explanation", nullable = false)
    private String explanation;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public Long getId() { return id; }
    public Long getProblemId() { return problemId; }
    public String getStepKey() { return stepKey; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getType() { return type; }
    public String getAnswer() { return answer; }
    public String getExplanation() { return explanation; }
    public int getSortOrder() { return sortOrder; }
}
