package com.powergrid.management.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "enterprises")
public class Enterprise implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "association_id")
    private Association association;

    // Конструкторы
    public Enterprise() {}

    public Enterprise(String name) {
        this.name = name;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Association getAssociation() { return association; }
    public void setAssociation(Association association) { this.association = association; }
}