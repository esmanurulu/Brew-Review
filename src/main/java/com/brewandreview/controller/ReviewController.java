package com.brewandreview.controller;

import com.brewandreview.model.*;
import com.brewandreview.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Controller
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private VisitRepository visitRepository;

    @GetMapping("/cafe/{cafeId}/review")
    public String showReviewForm(@PathVariable Long cafeId, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        // GÜVENLİK KONTROLÜ: Ziyaret etmiş mi?
        boolean hasVisited = visitRepository.existsByUser_UserIdAndCafe_CafeId(currentUser.getUserId(), cafeId);
        if (!hasVisited) {
            return "redirect:/cafe/" + cafeId + "?error=not_visited";
        }

        Cafe cafe = cafeRepository.findById(cafeId).orElse(null);
        if (cafe == null)
            return "redirect:/cafes";

        model.addAttribute("cafe", cafe);
        return "review-form";
    }

    @PostMapping("/cafe/{cafeId}/review")
    public String submitReview(@PathVariable Long cafeId,
            @RequestParam(required = false) List<Long> consumedItems,
            @RequestParam Double rating,
            @RequestParam String comment,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        Cafe cafe = cafeRepository.findById(cafeId).get();

        Review review = new Review();
        review.setUser(currentUser);
        review.setCafe(cafe);
        review.setRatingOverall(BigDecimal.valueOf(rating));
        review.setComment(comment);
        review.setReviewDate(Timestamp.from(Instant.now()));
        review.setReviewType("cafe");

        if (consumedItems != null && !consumedItems.isEmpty()) {
            List<MenuItem> items = menuItemRepository.findAllById(consumedItems);
            review.setConsumedItems(items);
        }

        reviewRepository.save(review);
        updateCafeRating(cafe);

        return "redirect:/cafe/" + cafeId;
    }

    // Yorumlarım Sayfası
    @GetMapping("/my-reviews")
    public String showMyReviews(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";
        List<Review> myReviews = reviewRepository.findByUser_UserId(currentUser.getUserId());
        model.addAttribute("reviews", myReviews);
        return "my-reviews";
    }

    // Silme
    @PostMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null && review.getUser().getUserId().equals(currentUser.getUserId())) {
            reviewRepository.delete(review);
            updateCafeRating(review.getCafe());
        }
        return "redirect:/my-reviews";
    }

    private void updateCafeRating(Cafe cafe) {
        List<Review> reviews = reviewRepository.findByCafe_CafeId(cafe.getCafeId());
        double sum = 0;
        for (Review r : reviews) {
            sum += r.getRatingOverall().doubleValue();
        }
        if (!reviews.isEmpty()) {
            double average = sum / reviews.size();
            cafe.setTotalRating(BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
        } else {
            cafe.setTotalRating(null);
        }
        cafeRepository.save(cafe);
    }

    @PostMapping("/review/{id}/helpful")
    public String markHelpful(@PathVariable Long id, HttpSession session) {
        // Giriş yapmayan da beğenebilsin mi? Hayır, giriş zorunlu olsun.
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            // Mevcut sayıyı al, 1 ekle ve kaydet
            int currentCount = (review.getHelpfulCount() == null) ? 0 : review.getHelpfulCount();
            review.setHelpfulCount(currentCount + 1);
            reviewRepository.save(review);
        }

        // İşlem bitince kullanıcının geldiği sayfaya geri dön (Referer)
        // Basitçe o yorumun ait olduğu kafeye dönelim:
        if (review != null && review.getCafe() != null) {
            return "redirect:/cafe/" + review.getCafe().getCafeId();
        }
        return "redirect:/cafes";
    }
}