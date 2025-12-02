package com.brewandreview.controller;

import com.brewandreview.model.Cafe;
import com.brewandreview.model.User;
import com.brewandreview.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    // YARDIMCI METOD: Gün İsimlerini Türkçeye Çevir
    private String translateDay(String day) {
        if (day == null)
            return "";
        switch (day) {
            case "Monday":
                return "Pazartesi";
            case "Tuesday":
                return "Salı";
            case "Wednesday":
                return "Çarşamba";
            case "Thursday":
                return "Perşembe";
            case "Friday":
                return "Cuma";
            case "Saturday":
                return "Cumartesi";
            case "Sunday":
                return "Pazar";
            default:
                return day;
        }
    }

    @GetMapping("/cafes")
    public String listCafes(@RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) boolean dessert,
            @RequestParam(required = false, defaultValue = "default") String sort,
            Model model) {

        Sort sortingRule = Sort.unsorted();
        if ("az".equals(sort)) {
            sortingRule = Sort.by(Sort.Direction.ASC, "name");
        } else if ("rating".equals(sort)) {
            sortingRule = Sort.by(Sort.Direction.DESC, "totalRating");
        }

        List<Cafe> cafes;
        if (name != null && !name.isEmpty()) {
            cafes = cafeRepository.findByNameContainingIgnoreCase(name, sortingRule);
        } else if (city != null && !city.isEmpty()) {
            cafes = cafeRepository.findByCityContainingIgnoreCase(city, sortingRule);
        } else if (dessert) {
            cafes = cafeRepository.findByHasDessertTrue(sortingRule);
        } else {
            cafes = cafeRepository.findAll(sortingRule);
        }

        model.addAttribute("cafes", cafes);
        model.addAttribute("cityList", cafeRepository.findDistinctCities());

        return "cafes";
    }

    @GetMapping("/cafe/{id}")
    public String getCafeDetails(@PathVariable Long id, Model model, HttpSession session) {
        Cafe cafe = cafeRepository.findById(id).orElse(null);

        if (cafe != null) {
            model.addAttribute("cafe", cafe);
            model.addAttribute("reviews", reviewRepository.findByCafe_CafeId(id));

            // --- YOĞUNLUK İSTATİSTİĞİ (TÜRKÇE GÜN İLE) ---
            List<Object[]> busyDay = visitRepository.findBusiestDay(id);
            List<Object[]> busyHour = visitRepository.findBusiestHour(id);

            String busiestTime = "Henüz yeterli veri yok";
            if (!busyDay.isEmpty() && !busyHour.isEmpty()) {
                String day = (String) busyDay.get(0)[0];
                Integer hour = (Integer) busyHour.get(0)[0];
                busiestTime = translateDay(day) + " günü, saat " + hour + ":00 civarı";
            }
            model.addAttribute("busiestTime", busiestTime);
            // ---------------------------------------------

            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                boolean visited = visitRepository.existsByUser_UserIdAndCafe_CafeId(currentUser.getUserId(), id);
                boolean isFavorite = favoriteRepository.existsByUserAndCafe(currentUser, cafe);

                model.addAttribute("visited", visited);
                model.addAttribute("isFavorite", isFavorite);

                var follows = followRepository.findByUser_UserId(currentUser.getUserId());
                List<Long> followedEmployeeIds = follows.stream()
                        .filter(f -> f.getEmployee() != null)
                        .map(f -> f.getEmployee().getEmployeeId())
                        .collect(Collectors.toList());

                model.addAttribute("followedEmployeeIds", followedEmployeeIds);

            } else {
                model.addAttribute("visited", false);
                model.addAttribute("isFavorite", false);
                model.addAttribute("followedEmployeeIds", new ArrayList<Long>());
            }

            return "cafe-detail";
        } else {
            return "redirect:/cafes";
        }
    }
}