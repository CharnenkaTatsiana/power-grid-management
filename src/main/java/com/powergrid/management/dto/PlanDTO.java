package com.powergrid.management.dto;

import java.time.Year;
import java.time.LocalDateTime;

public class PlanDTO {
    private Long id;
    private Year planYear;
    private String branchName;
    private String enterpriseName;
    private String associationName;
    private String createdBy;
    private LocalDateTime createdDate;

    public PlanDTO(Long id, Integer planYear,
                   String branchName, String enterpriseName, String associationName,
                   String createdBy, LocalDateTime createdDate) {
        this.id = id;
        this.planYear = planYear != null ? Year.of(planYear) : null;
        this.branchName = branchName;
        this.enterpriseName = enterpriseName;
        this.associationName = associationName;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    // Геттеры
    public Long getId() { return id; }
    public Year getPlanYear() { return planYear; }
    public String getBranchName() { return branchName; }
    public String getEnterpriseName() { return enterpriseName; }
    public String getAssociationName() { return associationName; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedDate() { return createdDate; }
}