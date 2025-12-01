package com.brewandreview.controller;

import com.brewandreview.model.Cafe;
import com.brewandreview.model.User;
import com.brewandreview.repository.CafeRepository;
import com.brewandreview.repository.FavoriteRepository;
import com.brewandreview.repository.ReviewRepository;
import com.brewandreview.repository.VisitRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class CafeController {

    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;

    @GetMapping("/cafes")
    public String listCafes(@RequestParam(required = false) String city,
            @RequestParam(required = false) boolean dessert,
            Model model) {
        List<Cafe> cafes;
        if (city != null && !city.isEmpty()) {
            cafes = cafeRepository.findByCityContainingIgnoreCase(city);
        } else if (dessert) {
            cafes = cafeRepository.findByHasDessertTrue();
        } else {
            cafes = cafeRepository.findAll();
        }
        model.addAttribute("cafes", cafes);
        return "cafes";
    }

    @GetMapping("/cafe/{id}")
    public String getCafeDetails(@PathVariable Long id, Model model, HttpSession session) {
        Cafe cafe = cafeRepository.findById(id).orElse(null);

        if (cafe != null) {
            model.addAttribute("cafe", cafe);
            model.addAttribute("reviews", reviewRepository.findByCafe_CafeId(id));

            // Kullanıcıya Özel Kontroller
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                boolean visited = visitRepository.existsByUser_UserIdAndCafe_CafeId(currentUser.getUserId(), id);
                boolean isFavorite = favoriteRepository.existsByUserAndCafe(currentUser, cafe);

                model.addAttribute("visited", visited);
                model.addAttribute("isFavorite", isFavorite);
            } else {
                model.addAttribute("visited", false);
                model.addAttribute("isFavorite", false);
            }

            return "cafe-detail";
        } else {
            return "redirect:/cafes";
        }
    }
}