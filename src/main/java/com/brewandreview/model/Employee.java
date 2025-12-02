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

    // --- YENİ EKLENENLER (Giriş ve Yönetim İçin) ---

    @Column(unique = true)
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    // Yönetici Kaydı için TC / Sicil No
    @Column(name = "citizen_id", unique = true, length = 11)
    private String citizenId;

    // Bir Manager sadece 1 kafeyi yönetir
    @OneToOne
    @JoinColumn(name = "managed_cafe_id")
    private Cafe managedCafe;

    // --- Getter ve Setterlar ---

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public Cafe getManagedCafe() {
        return managedCafe;
    }

    public void setManagedCafe(Cafe managedCafe) {
        this.managedCafe = managedCafe;
    }

    @ManyToMany(mappedBy = "employees")
    private java.util.List<Cafe> cafes;

    public java.util.List<Cafe> getCafes() {
        return cafes;
    }

    public void setCafes(java.util.List<Cafe> cafes) {
        this.cafes = cafes;
    }

}