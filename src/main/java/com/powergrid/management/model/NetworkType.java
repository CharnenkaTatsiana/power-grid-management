package com.powergrid.management.model;

public enum NetworkType {
    MAIN_NETWORK("Основная сеть"),
    DISTRIBUTION_NETWORK("Распределительная сеть");


    private final String displayName;

    NetworkType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}