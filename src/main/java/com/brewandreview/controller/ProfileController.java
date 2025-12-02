package com.brewandreview.controller;

import com.brewandreview.model.*;
import com.brewandreview.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        User dbUser = userRepository.findById(currentUser.getUserId()).get();
        model.addAttribute("user", dbUser);
        model.addAttribute("isOwnProfile", true);

        // İstatistikler
        model.addAttribute("visitCount", visitRepository.countByUser_UserId(dbUser.getUserId()));
        model.addAttribute("reviewCount", reviewRepository.countByUser_UserId(dbUser.getUserId()));

        // Listeler
        model.addAttribute("visits", visitRepository.findByUser_UserId(dbUser.getUserId()));
        model.addAttribute("reviews", reviewRepository.findByUser_UserId(dbUser.getUserId()));
        model.addAttribute("favorites", favoriteRepository.findByUser_UserId(dbUser.getUserId()));
        model.addAttribute("following", followRepository.findByUser_UserId(dbUser.getUserId()));

        // --- BARİSTA KONTROLÜ ---
        Employee employeeProfile = employeeRepository.findByUsername(dbUser.getUsername());

        if (employeeProfile != null) {
            model.addAttribute("isBarista", true);
            model.addAttribute("employeeData", employeeProfile);

            List<Follow> followers = followRepository.findByEmployee_EmployeeId(employeeProfile.getEmployeeId());
            model.addAttribute("followers", followers);
            model.addAttribute("followerCount", followers.size());

            // YENİ: Hakkımda Yapılan Yorumlar
            List<Review> myWorkReviews = reviewRepository.findByEmployee_EmployeeId(employeeProfile.getEmployeeId());
            model.addAttribute("workReviews", myWorkReviews);

        } else {
            model.addAttribute("isBarista", false);
        }

        return "profile";
    }

    @GetMapping("/user/{username}")
    public String showUserProfile(@PathVariable String username, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        User targetUser = userRepository.findByUsername(username);

        if (targetUser == null)
            return "redirect:/";
        if (currentUser != null && currentUser.getUsername().equals(username))
            return "redirect:/profile";

        model.addAttribute("user", targetUser);
        model.addAttribute("visitCount", visitRepository.countByUser_UserId(targetUser.getUserId()));
        model.addAttribute("reviewCount", reviewRepository.countByUser_UserId(targetUser.getUserId()));
        model.addAttribute("reviews", reviewRepository.findByUser_UserId(targetUser.getUserId()));
        model.addAttribute("favorites", favoriteRepository.findByUser_UserId(targetUser.getUserId()));

        Employee employeeProfile = employeeRepository.findByUsername(targetUser.getUsername());
        if (employeeProfile != null) {
            model.addAttribute("isBarista", true);
            model.addAttribute("employeeData", employeeProfile);
            model.addAttribute("followerCount",
                    followRepository.findByEmployee_EmployeeId(employeeProfile.getEmployeeId()).size());

            if (currentUser != null) {
                boolean isFollowing = followRepository.existsByUserAndEmployee(currentUser, employeeProfile);
                model.addAttribute("isFollowing", isFollowing);
            }
        } else {
            model.addAttribute("isBarista", false);
        }

        return "user-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String favoriteDrink, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        User dbUser = userRepository.findById(currentUser.getUserId()).get();
        if (favoriteDrink != null)
            dbUser.setFavoriteDrink(favoriteDrink);

        userRepository.save(dbUser);
        session.setAttribute("currentUser", dbUser);
        return "redirect:/profile";
    }
}