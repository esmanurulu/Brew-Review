package com.brewandreview.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "Review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "review_type", nullable = false)
    private String reviewType;

    @Column(name = "rating_overall")
    private BigDecimal ratingOverall;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "review_date")
    private Timestamp reviewDate;

    // --- YENİ EKLENEN: FAYDALI SAYISI ---
    @Column(name = "helpful_count")
    private Integer helpfulCount;

    // --- İLİŞKİLER ---
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private MenuItem menuItem;

    @ManyToMany
    @JoinTable(name = "Review_Consumed_Items", joinColumns = @JoinColumn(name = "review_id"), inverseJoinColumns = @JoinColumn(name = "menu_id"))
    private List<MenuItem> consumedItems;

    // --- Getter ve Setterlar ---

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public BigDecimal getRatingOverall() {
        return ratingOverall;
    }

    public void setRatingOverall(BigDecimal ratingOverall) {
        this.ratingOverall = ratingOverall;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Timestamp reviewDate) {
        this.reviewDate = reviewDate;
    }

    // YENİ GETTER VE SETTER
    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public List<MenuItem> getConsumedItems() {
        return consumedItems;
    }

    public void setConsumedItems(List<MenuItem> consumedItems) {
        this.consumedItems = consumedItems;
    }
}