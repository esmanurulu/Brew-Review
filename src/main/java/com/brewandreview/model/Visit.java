package com.brewandreview.model;

import jakarta.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "Visit")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Long visitId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @Column(name = "visit_date", nullable = false)
    private Date visitDate;

    @Column(name = "visit_time")
    private Time visitTime;

    @Column(name = "visit_note", columnDefinition = "TEXT")
    private String visitNote;

    // Getter ve Setterlar
    public Long getVisitId() {
        return visitId;
    }

    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Cafe getCafe() {
        return cafe;
    }

    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Time getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Time visitTime) {
        this.visitTime = visitTime;
    }

    public String getVisitNote() {
        return visitNote;
    }

    public void setVisitNote(String visitNote) {
        this.visitNote = visitNote;
    }
}