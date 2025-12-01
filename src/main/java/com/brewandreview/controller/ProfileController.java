package com.brewandreview.controller;

import com.brewandreview.model.*;
import com.brewandreview.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        model.addAttribute("user", currentUser);

        // İstatistikler
        long visitCount = visitRepository.countByUser_UserId(currentUser.getUserId());
        long reviewCount = reviewRepository.countByUser_UserId(currentUser.getUserId());

        model.addAttribute("visitCount", visitCount);
        model.addAttribute("reviewCount", reviewCount);

        // Listeler
        List<Visit> myVisits = visitRepository.findByUser_UserId(currentUser.getUserId());
        List<Review> myReviews = reviewRepository.findByUser_UserId(currentUser.getUserId());
        List<Favorite> myFavorites = favoriteRepository.findByUser_UserId(currentUser.getUserId());

        model.addAttribute("visits", myVisits);
        model.addAttribute("reviews", myReviews);
        model.addAttribute("favorites", myFavorites);

        return "profile";
    }
}