package com.brewandreview.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Cafe")
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cafe_id")
    private Long cafeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(name = "total_rating")
    private BigDecimal totalRating;

    @Column(name = "has_dessert")
    private Boolean hasDessert;

    @Column(name = "review_count")
    private Integer reviewCount;

    // --- YENİ EKLENEN İLİŞKİLER ---

    // Bir kafenin birden çok menü öğesi olur (Cafe_Menu tablosu üzerinden)
    @ManyToMany
    @JoinTable(name = "Cafe_Menu", joinColumns = @JoinColumn(name = "cafe_id"), inverseJoinColumns = @JoinColumn(name = "menu_id"))
    private List<MenuItem> menuItems;

    // Bir kafenin birden çok çalışanı olur (Employee_Cafe tablosu üzerinden)
    @ManyToMany
    @JoinTable(name = "Employee_Cafe", joinColumns = @JoinColumn(name = "cafe_id"), inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private List<Employee> employees;

    // --- Getter ve Setterlar ---

    public Long getCafeId() {
        return cafeId;
    }

    public void setCafeId(Long cafeId) {
        this.cafeId = cafeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BigDecimal getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(BigDecimal totalRating) {
        this.totalRating = totalRating;
    }

    public Boolean getHasDessert() {
        return hasDessert;
    }

    public void setHasDessert(Boolean hasDessert) {
        this.hasDessert = hasDessert;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    // Yeni listeler için getter/setter
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}