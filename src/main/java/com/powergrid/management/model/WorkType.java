package com.powergrid.management.model;

import javax.persistence.*;

@Entity
@Table(name = "work_types")
public class WorkType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "network_type", length = 50)
    private NetworkType networkType;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Конструкторы
    public WorkType() {}

    public WorkType(String name, NetworkType networkType) {
        this.name = name;
        this.networkType = networkType;
    }

    public WorkType(String name, String description, NetworkType networkType) {
        this.name = name;
        this.description = description;
        this.networkType = networkType;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public NetworkType getNetworkType() { return networkType; }
    public void setNetworkType(NetworkType networkType) { this.networkType = networkType; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    @Override
    public String toString() {
        return "WorkType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", networkType=" + networkType +
                '}';
    }
}