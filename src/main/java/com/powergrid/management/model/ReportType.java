package com.powergrid.management.model;

public enum ReportType {

    MONTHLY("Ежемесячный"),
    ANNUAL("Годовой"),
    OPERATIONAL("Оперативный"),
    ANALYTICAL("Фактический");


    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
