package com.algoquest.backend.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "problem_judge_specs")
public class JudgeSpecEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "problem_id", nullable = false, unique = true)
    private Long problemId;

    @Column(name = "method_name", nullable = false)
    private String methodName;

    @Column(name = "params", nullable = false, columnDefinition = "jsonb")
    private String params;

    @Column(name = "return_type", nullable = false)
    private String returnType;

    @Column(name = "output_target")
    private String outputTarget;

    @Column(name = "comparison_strategy", nullable = false)
    private String comparisonStrategy;

    @Column(name = "comparison_options", columnDefinition = "jsonb")
    private String comparisonOptions;

    @Column(name = "time_limit_ms", nullable = false)
    private int timeLimitMs;

    @Column(name = "memory_limit_mb", nullable = false)
    private int memoryLimitMb;

    public Long getId() { return id; }
    public Long getProblemId() { return problemId; }
    public String getMethodName() { return methodName; }
    public String getParams() { return params; }
    public String getReturnType() { return returnType; }
    public String getOutputTarget() { return outputTarget; }
    public String getComparisonStrategy() { return comparisonStrategy; }
    public String getComparisonOptions() { return comparisonOptions; }
    public int getTimeLimitMs() { return timeLimitMs; }
    public int getMemoryLimitMb() { return memoryLimitMb; }
}
