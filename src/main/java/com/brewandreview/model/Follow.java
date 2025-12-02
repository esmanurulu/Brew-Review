package com.brewandreview.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "Follow")
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long followId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Takip eden kişi

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee; // Takip edilen çalışan

    @ManyToOne
    @JoinColumn(name = "cafe_id")
    private Cafe cafe; // Takip edilen kafe

    @ManyToOne
    @JoinColumn(name = "followed_user_id") // YENİ
    private User followedUser; // Takip edilen başka bir kullanıcı

    @Column(name = "follow_date")
    private Timestamp followDate;

    // Getter - Setter
    public Long getFollowId() {
        return followId;
    }

    public void setFollowId(Long followId) {
        this.followId = followId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Cafe getCafe() {
        return cafe;
    }

    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }

    public User getFollowedUser() {
        return followedUser;
    }

    public void setFollowedUser(User followedUser) {
        this.followedUser = followedUser;
    }

    public Timestamp getFollowDate() {
        return followDate;
    }

    public void setFollowDate(Timestamp followDate) {
        this.followDate = followDate;
    }
}