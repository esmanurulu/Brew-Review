package com.brewandreview.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(nullable = false)
    private String name;

    @Column(name = "experience_years")
    private Integer experienceYears;

    private String role; // 'barista', 'manager'

    // Getter ve Setterlar
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}