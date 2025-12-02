package com.brewandreview.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    // --- YENİ EKLENEN: İŞYERİ RUHSAT NO ---
    // Her kafenin benzersiz bir ruhsat numarası olur
    @Column(name = "license_number", unique = true, length = 20)
    private String licenseNumber;

    // --- İLİŞKİLER ---
    @ManyToMany
    @JoinTable(name = "Cafe_Menu", joinColumns = @JoinColumn(name = "cafe_id"), inverseJoinColumns = @JoinColumn(name = "menu_id"))
    private List<MenuItem> menuItems;

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

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

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

    public boolean isOpenNow() {
        if (this.openingHours == null || !this.openingHours.contains("-")) {
            return false; // Format bozuksa kapalı varsay
        }
        try {
            // Örn: "09:00-23:00"
            String[] parts = this.openingHours.split("-");
            String startStr = parts[0].trim();
            String endStr = parts[1].trim();

            LocalTime now = LocalTime.now();
            LocalTime start = LocalTime.parse(startStr, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(endStr, DateTimeFormatter.ofPattern("HH:mm"));

            // Gece yarısı geçişini kontrol et (Örn: 18:00 - 02:00)
            if (end.isBefore(start)) {
                return now.isAfter(start) || now.isBefore(end);
            } else {
                return now.isAfter(start) && now.isBefore(end);
            }
        } catch (Exception e) {
            return false; // Hata olursa kapalı göster
        }
    }

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "opening_hours")
    private String openingHours;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

}