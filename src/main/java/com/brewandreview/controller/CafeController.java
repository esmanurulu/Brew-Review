package com.brewandreview.controller;

import com.brewandreview.model.Cafe;
import com.brewandreview.repository.CafeRepository;
import com.brewandreview.repository.ReviewRepository;
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
    private ReviewRepository reviewRepository; // İşçi 2: Yorumları getirecek

    // Kafe Listeleme
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

    // Kafe Detay Sayfası
    @GetMapping("/cafe/{id}")
    public String getCafeDetails(@PathVariable Long id, Model model) {
        // 1. Kafeyi bul ve kutuya koy
        Cafe cafe = cafeRepository.findById(id).orElse(null);

        if (cafe != null) {
            model.addAttribute("cafe", cafe);

            // --- EKSİK OLAN PARÇA BURASIYDI ---
            // 2. Bu kafeye ait yorumları bul ve kutuya koy
            model.addAttribute("reviews", reviewRepository.findByCafe_CafeId(id));
            // ----------------------------------

            return "cafe-detail";
        } else {
            return "redirect:/cafes";
        }
    }
}