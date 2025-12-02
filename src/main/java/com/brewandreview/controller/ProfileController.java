package com.brewandreview.controller;

import com.brewandreview.model.*;
import com.brewandreview.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

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

    // 1. KENDİ PROFİLİM (DASHBOARD)
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        User dbUser = userRepository.findById(currentUser.getUserId()).get();
        model.addAttribute("user", dbUser);
        model.addAttribute("isOwnProfile", true); // Kendi profili olduğunu belirt

        // İstatistikler
        model.addAttribute("visitCount", visitRepository.countByUser_UserId(dbUser.getUserId()));
        model.addAttribute("reviewCount", reviewRepository.countByUser_UserId(dbUser.getUserId()));

        // Listeler
        model.addAttribute("visits", visitRepository.findByUser_UserId(dbUser.getUserId()));
        model.addAttribute("reviews", reviewRepository.findByUser_UserId(dbUser.getUserId()));
        model.addAttribute("favorites", favoriteRepository.findByUser_UserId(dbUser.getUserId()));
        model.addAttribute("following", followRepository.findByUser_UserId(dbUser.getUserId())); // Takip Ettiklerim

        // --- BARİSTA ÖZEL ALANI ---
        // Eğer bu kullanıcı aynı zamanda bir çalışansa (username eşleşmesi ile
        // buluyoruz)
        Employee employeeProfile = employeeRepository.findByUsername(dbUser.getUsername());

        if (employeeProfile != null) {
            model.addAttribute("isBarista", true);
            model.addAttribute("employeeData", employeeProfile);

            // Baristayı takip edenler
            List<Follow> followers = followRepository.findByEmployee_EmployeeId(employeeProfile.getEmployeeId());
            model.addAttribute("followers", followers);
            model.addAttribute("followerCount", followers.size());
        } else {
            model.addAttribute("isBarista", false);

            // Normal kullanıcıyı takip edenler (Eğer bu özelliği açtıysak)
            List<Follow> followers = followRepository.findByFollowedUser_UserId(dbUser.getUserId());
            model.addAttribute("followers", followers);
            model.addAttribute("followerCount", followers.size());
        }

        return "profile";
    }

    // 2. BAŞKASININ PROFİLİ (Username ile)
    @GetMapping("/user/{username}")
    public String showUserProfile(@PathVariable String username, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");

        // Kullanıcıyı isminden bul
        User targetUser = userRepository.findByUsername(username);

        // Eğer kullanıcı yoksa (belki sadece employee tablosunda vardır ama user
        // olmamıştır)
        if (targetUser == null) {
            // Belki de Employee tablosunda vardır?
            Employee emp = employeeRepository.findByUsername(username);
            if (emp != null) {
                // Eğer employee ise ama user değilse (nadir durum), onu employee olarak göster
                // Ama biz hepsini user yaptık, o yüzden bu ihtimal düşük.
                return "redirect:/";
            }
            return "redirect:/";
        }

        // Eğer kendi profiline tıkladıysa ana profile yönlendir
        if (currentUser != null && currentUser.getUsername().equals(username)) {
            return "redirect:/profile";
        }

        model.addAttribute("user", targetUser);
        model.addAttribute("isOwnProfile", false);

        // İstatistikler
        model.addAttribute("visitCount", visitRepository.countByUser_UserId(targetUser.getUserId()));
        model.addAttribute("reviewCount", reviewRepository.countByUser_UserId(targetUser.getUserId()));
        model.addAttribute("reviews", reviewRepository.findByUser_UserId(targetUser.getUserId()));
        model.addAttribute("favorites", favoriteRepository.findByUser_UserId(targetUser.getUserId()));

        // Barista Kontrolü
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

    // 3. PROFİL GÜNCELLEME
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String favoriteDrink, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        User dbUser = userRepository.findById(currentUser.getUserId()).get();
        if (favoriteDrink != null) {
            dbUser.setFavoriteDrink(favoriteDrink);
        }
        userRepository.save(dbUser);
        session.setAttribute("currentUser", dbUser);

        return "redirect:/profile";
    }
}