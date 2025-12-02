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
    @Autowired
    private CafeRepository cafeRepository;

    // KENDİ PROFİLİM DASHBOARD
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        // Kullanıcıyı veritabanından çekiyorum
        User dbUser = userRepository.findById(currentUser.getUserId()).get();
        model.addAttribute("user", dbUser);
        model.addAttribute("isOwnProfile", true);

        // İstatistikler
        long visitCount = visitRepository.countByUser_UserId(dbUser.getUserId());
        long reviewCount = reviewRepository.countByUser_UserId(dbUser.getUserId());

        model.addAttribute("visitCount", visitCount);
        model.addAttribute("reviewCount", reviewCount);

        List<Visit> myVisits = visitRepository.findByUser_UserId(dbUser.getUserId());
        List<Review> myReviews = reviewRepository.findByUser_UserId(dbUser.getUserId());
        List<Favorite> myFavorites = favoriteRepository.findByUser_UserId(dbUser.getUserId());
        List<Follow> myFollows = followRepository.findByUser_UserId(dbUser.getUserId());

        model.addAttribute("visits", myVisits);
        model.addAttribute("reviews", myReviews);
        model.addAttribute("favorites", myFavorites);
        model.addAttribute("following", myFollows);

        // --- BARİSTA ÖZEL ALANI ---
        // Eğer bu kullanıcı aynı zamanda bir çalışansa (username eşleşmesi ile
        // buluyorum)
        Employee employeeProfile = employeeRepository.findByUsername(dbUser.getUsername());

        if (employeeProfile != null) {
            model.addAttribute("isBarista", true);
            model.addAttribute("employeeData", employeeProfile);

            List<Follow> followers = followRepository.findByEmployee_EmployeeId(employeeProfile.getEmployeeId());
            model.addAttribute("followers", followers);
            model.addAttribute("followerCount", followers.size());

            // Baristaya yapılan iş yorumları
            List<Review> myWorkReviews = reviewRepository.findByEmployee_EmployeeId(employeeProfile.getEmployeeId());
            model.addAttribute("workReviews", myWorkReviews);
        } else {
            model.addAttribute("isBarista", false);
            model.addAttribute("followers", null);
            model.addAttribute("followerCount", 0);
        }

        return "profile";
    }

    @GetMapping("/user/{username}")
    public String showUserProfile(@PathVariable String username, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");

        User targetUser = userRepository.findByUsername(username);

        if (targetUser == null)
            return "redirect:/";

        // Eğer kendi profiline tıkladıysa ana profile yönlendir
        if (currentUser != null && currentUser.getUsername().equals(username)) {
            return "redirect:/profile";
        }

        model.addAttribute("user", targetUser);
        model.addAttribute("isOwnProfile", false);

        model.addAttribute("visitCount", visitRepository.countByUser_UserId(targetUser.getUserId()));
        model.addAttribute("reviewCount", reviewRepository.countByUser_UserId(targetUser.getUserId()));
        model.addAttribute("reviews", reviewRepository.findByUser_UserId(targetUser.getUserId()));
        model.addAttribute("favorites", favoriteRepository.findByUser_UserId(targetUser.getUserId()));
        model.addAttribute("visits", visitRepository.findByUser_UserId(targetUser.getUserId()));

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
            model.addAttribute("isFollowing", false);
        }

        return "user-profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        User dbUser = userRepository.findById(currentUser.getUserId()).get();
        model.addAttribute("user", dbUser);

        Employee employee = employeeRepository.findByUsername(dbUser.getUsername());
        if (employee != null) {
            model.addAttribute("isBarista", true);
            model.addAttribute("allCafes", cafeRepository.findAll());
            model.addAttribute("employee", employee);
        } else {
            model.addAttribute("isBarista", false);
        }

        return "edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String email,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String favoriteDrink,
            @RequestParam(required = false) Long cafeId,
            HttpSession session,
            Model model) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        User dbUser = userRepository.findById(currentUser.getUserId()).get();

        if (newPassword != null && !newPassword.isEmpty()) {
            if (currentPassword == null || !dbUser.getPasswordHash().equals(currentPassword)) {
                model.addAttribute("error", "Mevcut şifreniz yanlış!");
                model.addAttribute("user", dbUser);

                Employee emp = employeeRepository.findByUsername(dbUser.getUsername());
                if (emp != null) {
                    model.addAttribute("isBarista", true);
                    model.addAttribute("allCafes", cafeRepository.findAll());
                    model.addAttribute("employee", emp);
                }
                return "edit-profile";
            }
            dbUser.setPasswordHash(newPassword);
        }

        dbUser.setEmail(email);
        if (favoriteDrink != null)
            dbUser.setFavoriteDrink(favoriteDrink);

        userRepository.save(dbUser);

        // BARİSTA KAFE GÜNCELLEME
        Employee employee = employeeRepository.findByUsername(dbUser.getUsername());
        if (employee != null && cafeId != null) {
            Cafe selectedCafe = cafeRepository.findById(cafeId).orElse(null);
            if (selectedCafe != null) {
                employee.getCafes().clear();
                employee.getCafes().add(selectedCafe);
                employeeRepository.save(employee);
            }
        }

        session.setAttribute("currentUser", dbUser);
        return "redirect:/profile";
    }


    @PostMapping("/profile/update")
    public String quickUpdate(@RequestParam String favoriteDrink, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            User dbUser = userRepository.findById(currentUser.getUserId()).get();
            dbUser.setFavoriteDrink(favoriteDrink);
            userRepository.save(dbUser);
            session.setAttribute("currentUser", dbUser);
        }
        return "redirect:/profile";
    }
}