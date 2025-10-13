package com.powergrid.management.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "report_items")
public class ReportItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne
    @JoinColumn(name = "work_type_id", nullable = false)
    private WorkType workType;

    // Плановые показатели
    @Column(name = "annual_plan")
    private Double annualPlan;

    @Column(name = "quarter_plan")
    private Double quarterPlan;

    // Фактические показатели
    @Column(name = "month_fact")
    private Double monthFact;

    @Column(name = "cumulative_fact")
    private Double cumulativeFact;

    // Проценты выполнения
    @Column(name = "annual_percentage")
    private Double annualPercentage;

    @Column(name = "quarter_percentage")
    private Double quarterPercentage;

    // Конструкторы
    public ReportItem() {}

    public ReportItem(Report report, WorkType workType) {
        this.report = report;
        this.workType = workType;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Report getReport() { return report; }
    public void setReport(Report report) { this.report = report; }

    public WorkType getWorkType() { return workType; }
    public void setWorkType(WorkType workType) { this.workType = workType; }

    public Double getAnnualPlan() { return annualPlan; }
    public void setAnnualPlan(Double annualPlan) { this.annualPlan = annualPlan; }

    public Double getQuarterPlan() { return quarterPlan; }
    public void setQuarterPlan(Double quarterPlan) { this.quarterPlan = quarterPlan; }

    public Double getMonthFact() { return monthFact; }
    public void setMonthFact(Double monthFact) { this.monthFact = monthFact; }

    public Double getCumulativeFact() { return cumulativeFact; }
    public void setCumulativeFact(Double cumulativeFact) { this.cumulativeFact = cumulativeFact; }

    public Double getAnnualPercentage() { return annualPercentage; }
    public void setAnnualPercentage(Double annualPercentage) { this.annualPercentage = annualPercentage; }

    public Double getQuarterPercentage() { return quarterPercentage; }
    public void setQuarterPercentage(Double quarterPercentage) { this.quarterPercentage = quarterPercentage; }

    // Метод для расчета процентов
    public void calculatePercentages() {
        if (annualPlan != null && annualPlan > 0 && cumulativeFact != null) {
            this.annualPercentage = (cumulativeFact / annualPlan) * 100;
        }
        if (quarterPlan != null && quarterPlan > 0 && cumulativeFact != null) {
            this.quarterPercentage = (cumulativeFact / quarterPlan) * 100;
        }
    }
}