package com.algoquest.backend.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "problems")
public class ProblemEntity {

    @Id
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "starter_code", nullable = false)
    private String starterCode;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "reference_solution")
    private String referenceSolution;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDifficulty() { return difficulty; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getStarterCode() { return starterCode; }
    public int getSortOrder() { return sortOrder; }
    public String getStatus() { return status; }
    public String getSource() { return source; }
    public String getReferenceSolution() { return referenceSolution; }
}
