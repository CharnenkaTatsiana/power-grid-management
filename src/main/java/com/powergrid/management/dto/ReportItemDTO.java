package com.powergrid.management.dto;

public class ReportItemDTO {
    private Long id;
    private String workTypeName;
    private Double annualPlan;
    private Double quarterPlan;
    private Double monthFact;
    private Double cumulativeFact;
    private Double annualPercentage;
    private Double quarterPercentage;

    // Конструктор для обновления фактических данных
    public ReportItemDTO(Long id, Double monthFact) {
        this.id = id;
        this.monthFact = monthFact;
    }

    // Полный конструктор
    public ReportItemDTO(Long id, String workTypeName,
                         Double annualPlan, Double quarterPlan,
                         Double monthFact, Double cumulativeFact,
                         Double annualPercentage, Double quarterPercentage) {
        this.id = id;
        this.workTypeName = workTypeName;
        this.annualPlan = annualPlan;
        this.quarterPlan = quarterPlan;
        this.monthFact = monthFact;
        this.cumulativeFact = cumulativeFact;
        this.annualPercentage = annualPercentage;
        this.quarterPercentage = quarterPercentage;
    }

    // Геттеры
    public Long getId() { return id; }
    public String getWorkTypeName() { return workTypeName; }
    public Double getAnnualPlan() { return annualPlan; }
    public Double getQuarterPlan() { return quarterPlan; }
    public Double getMonthFact() { return monthFact; }
    public Double getCumulativeFact() { return cumulativeFact; }
    public Double getAnnualPercentage() { return annualPercentage; }
    public Double getQuarterPercentage() { return quarterPercentage; }
}