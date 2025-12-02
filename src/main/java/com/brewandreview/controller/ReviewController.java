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
    private VisitRepository visitRepository; // Eklendi

    // 1. Yorum Formunu Göster
    @GetMapping("/cafe/{cafeId}/review")
    public String showReviewForm(@PathVariable Long cafeId, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        // Ziyaret Kontrolü
        boolean hasVisited = visitRepository.existsByUser_UserIdAndCafe_CafeId(currentUser.getUserId(), cafeId);
        if (!hasVisited)
            return "redirect:/cafe/" + cafeId + "?error=not_visited";

        Cafe cafe = cafeRepository.findById(cafeId).orElse(null);
        if (cafe == null)
            return "redirect:/cafes";

        model.addAttribute("cafe", cafe);
        return "review-form";
    }

    // 2. Yorumu Kaydet (Barista Desteği Eklendi)
    @PostMapping("/cafe/{cafeId}/review")
    public String submitReview(@PathVariable Long cafeId,
            @RequestParam(required = false) List<Long> consumedItems,
            @RequestParam Double rating,
            @RequestParam String comment,
            @RequestParam(defaultValue = "cafe") String targetType, // 'cafe' veya 'employee'
            @RequestParam(required = false) Long targetId,
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
        review.setReviewType(targetType);

        // Barista Seçildiyse
        if ("employee".equals(targetType) && targetId != null) {
            review.setEmployee(employeeRepository.findById(targetId).orElse(null));
        }

        if (consumedItems != null && !consumedItems.isEmpty()) {
            List<MenuItem> items = menuItemRepository.findAllById(consumedItems);
            review.setConsumedItems(items);
        }

        reviewRepository.save(review);

        // Sadece Kafe puanı verildiyse ortalamayı güncelle
        if ("cafe".equals(targetType)) {
            updateCafeRating(cafe);
        }

        return "redirect:/cafe/" + cafeId;
    }

    // --- Diğer Metodlar (Listeleme, Silme, Like) ---

    @GetMapping("/my-reviews")
    public String showMyReviews(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";
        List<Review> myReviews = reviewRepository.findByUser_UserId(currentUser.getUserId());
        model.addAttribute("reviews", myReviews);
        return "my-reviews";
    }

    @PostMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null && review.getUser().getUserId().equals(currentUser.getUserId())) {
            reviewRepository.delete(review);
            if ("cafe".equals(review.getReviewType())) {
                updateCafeRating(review.getCafe());
            }
        }
        return "redirect:/my-reviews";
    }

    @PostMapping("/review/{id}/helpful")
    public String markHelpful(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            int currentCount = (review.getHelpfulCount() == null) ? 0 : review.getHelpfulCount();
            review.setHelpfulCount(currentCount + 1);
            reviewRepository.save(review);
            return "redirect:/cafe/" + review.getCafe().getCafeId();
        }
        return "redirect:/cafes";
    }

    private void updateCafeRating(Cafe cafe) {
        List<Review> reviews = reviewRepository.findByCafe_CafeId(cafe.getCafeId());
        double sum = 0;
        int count = 0;
        for (Review r : reviews) {
            // Sadece kafe türündeki yorumları ortalamaya kat
            if ("cafe".equals(r.getReviewType())) {
                sum += r.getRatingOverall().doubleValue();
                count++;
            }
        }
        if (count > 0) {
            double average = sum / count;
            cafe.setTotalRating(BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
        } else {
            cafe.setTotalRating(null);
        }
        cafeRepository.save(cafe);
    }
}