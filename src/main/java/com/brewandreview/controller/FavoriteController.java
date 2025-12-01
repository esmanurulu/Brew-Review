package com.brewandreview.controller;

import com.brewandreview.model.*;
import com.brewandreview.repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.time.Instant;

@Controller
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private CafeRepository cafeRepository;

    @PostMapping("/cafe/{cafeId}/favorite")
    @Transactional
    public String toggleFavorite(@PathVariable Long cafeId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        Cafe cafe = cafeRepository.findById(cafeId).orElse(null);
        if (cafe != null) {
            if (favoriteRepository.existsByUserAndCafe(currentUser, cafe)) {
                favoriteRepository.deleteByUserAndCafe(currentUser, cafe);
            } else {
                Favorite fav = new Favorite();
                fav.setUser(currentUser);
                fav.setCafe(cafe);
                fav.setFavoriteCategory("cafe");
                fav.setAddedDate(Timestamp.from(Instant.now()));
                favoriteRepository.save(fav);
            }
        }
        return "redirect:/cafe/" + cafeId;
    }
}