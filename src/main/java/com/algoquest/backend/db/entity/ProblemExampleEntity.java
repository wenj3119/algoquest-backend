package com.algoquest.backend.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "problem_examples")
public class ProblemExampleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "input", nullable = false)
    private String input;

    @Column(name = "output", nullable = false)
    private String output;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public Long getId() { return id; }
    public Long getProblemId() { return problemId; }
    public String getInput() { return input; }
    public String getOutput() { return output; }
    public String getExplanation() { return explanation; }
    public int getSortOrder() { return sortOrder; }
}
