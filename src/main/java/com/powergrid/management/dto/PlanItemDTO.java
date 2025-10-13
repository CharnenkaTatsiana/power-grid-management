package com.powergrid.management.dto;

public class PlanItemDTO {
    private Long id;
    private String workTypeName;
    private Double q1Plan;
    private Double q2Plan;
    private Double q3Plan;
    private Double q4Plan;
    private Double annualPlan;

    public PlanItemDTO(Long id, String workTypeName, Double q1Plan, Double q2Plan,
                       Double q3Plan, Double q4Plan, Double annualPlan) {
        this.id = id;
        this.workTypeName = workTypeName;
        this.q1Plan = q1Plan;
        this.q2Plan = q2Plan;
        this.q3Plan = q3Plan;
        this.q4Plan = q4Plan;
        this.annualPlan = annualPlan;
    }

    // Геттеры
    public Long getId() { return id; }
    public String getWorkTypeName() { return workTypeName; }
    public Double getQ1Plan() { return q1Plan; }
    public Double getQ2Plan() { return q2Plan; }
    public Double getQ3Plan() { return q3Plan; }
    public Double getQ4Plan() { return q4Plan; }
    public Double getAnnualPlan() { return annualPlan; }
}