package com.algoquest.backend.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "problem_test_cases")
public class TestCaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "display_input", nullable = false)
    private String displayInput;

    @Column(name = "inputs", nullable = false, columnDefinition = "jsonb")
    private String inputs;

    @Column(name = "expected", nullable = false, columnDefinition = "jsonb")
    private String expected;

    @Column(name = "is_sample", nullable = false)
    private boolean isSample;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public Long getId() { return id; }
    public Long getProblemId() { return problemId; }
    public String getDisplayInput() { return displayInput; }
    public String getInputs() { return inputs; }
    public String getExpected() { return expected; }
    public boolean isSample() { return isSample; }
    public int getSortOrder() { return sortOrder; }
}
