package com.powergrid.management.dto;

public class ReferenceItemDTO {
    private Long id;
    private String name;

    public ReferenceItemDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Геттеры
    public Long getId() { return id; }
    public String getName() { return name; }
}