package com.powergrid.management.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "plan_items")
public class PlanItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "work_type_id")
    private WorkType workType;

    private Double annualPlan;
    private Double q1Plan;
    private Double q2Plan;
    private Double q3Plan;
    private Double q4Plan;

    // Конструктор по умолчанию
    public PlanItem() {
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }

    public WorkType getWorkType() { return workType; }
    public void setWorkType(WorkType workType) { this.workType = workType; }

    public Double getAnnualPlan() { return annualPlan; }
    public void setAnnualPlan(Double annualPlan) { this.annualPlan = annualPlan; }

    public Double getQ1Plan() { return q1Plan; }
    public void setQ1Plan(Double q1Plan) { this.q1Plan = q1Plan; }

    public Double getQ2Plan() { return q2Plan; }
    public void setQ2Plan(Double q2Plan) { this.q2Plan = q2Plan; }

    public Double getQ3Plan() { return q3Plan; }
    public void setQ3Plan(Double q3Plan) { this.q3Plan = q3Plan; }

    public Double getQ4Plan() { return q4Plan; }
    public void setQ4Plan(Double q4Plan) { this.q4Plan = q4Plan; }
}