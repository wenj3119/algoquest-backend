package com.algoquest.backend.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "problem_step_options")
public class ProblemStepOptionEntity {

    @Id
    private Long id;

    @Column(name = "step_id", nullable = false)
    private Long stepId;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public Long getId() { return id; }
    public Long getStepId() { return stepId; }
    public String getLabel() { return label; }
    public String getContent() { return content; }
    public int getSortOrder() { return sortOrder; }
}
