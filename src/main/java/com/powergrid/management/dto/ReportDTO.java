package com.powergrid.management.dto;

import com.powergrid.management.model.ReportType;
import java.time.LocalDateTime;

public class ReportDTO {
    private Long id;
    private String title;
    private ReportType reportType;
    private LocalDateTime createdDate;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private String createdBy;
    private String branchName;
    private String enterpriseName;
    private String associationName;
    private Boolean isFilled;
    private String reportPeriod;

    // Конструктор для отчетов филиалов
    public ReportDTO(Long id, String title, ReportType reportType,
                     LocalDateTime createdDate, LocalDateTime periodStart, LocalDateTime periodEnd,
                     String createdBy, String branchName, Boolean isFilled) {
        this.id = id;
        this.title = title;
        this.reportType = reportType;
        this.createdDate = createdDate;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.createdBy = createdBy;
        this.branchName = branchName;
        this.isFilled = isFilled;
    }

    // Конструктор для отчетов РУП
    public ReportDTO(Long id, String title, ReportType reportType,
                     LocalDateTime createdDate, LocalDateTime periodStart, LocalDateTime periodEnd,
                     String createdBy, String enterpriseName, String associationName, Boolean isFilled) {
        this.id = id;
        this.title = title;
        this.reportType = reportType;
        this.createdDate = createdDate;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.createdBy = createdBy;
        this.enterpriseName = enterpriseName;
        this.associationName = associationName;
        this.isFilled = isFilled;
    }

    // Геттеры
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public ReportType getReportType() { return reportType; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getPeriodStart() { return periodStart; }
    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public String getCreatedBy() { return createdBy; }
    public String getBranchName() { return branchName; }
    public String getEnterpriseName() { return enterpriseName; }
    public void setEnterpriseName(String enterpriseName) { this.enterpriseName = enterpriseName; }
    public String getAssociationName() { return associationName; }
    public Boolean getIsFilled() { return isFilled; }
    public String getReportPeriod() { return reportPeriod; }
    public void setReportPeriod(String reportPeriod) { this.reportPeriod = reportPeriod; }
}