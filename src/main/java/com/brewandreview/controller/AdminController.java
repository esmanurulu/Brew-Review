package com.brewandreview.controller;

import com.brewandreview.model.Cafe;
import com.brewandreview.model.Employee;
import com.brewandreview.model.User;
import com.brewandreview.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.time.Instant;
import java.math.BigDecimal;

@Controller
public class AdminController {

    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private UserRepository userRepository;

    // en yogun gun yazisi icin turkce translatei
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

    // kafe sahibi fonskiyonlari dashboardi
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        // Yönettiği kafeyi veritabanından çekiyorum
        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).orElse(null);

        if (dbManager != null && dbManager.getManagedCafe() != null) {
            Cafe cafe = dbManager.getManagedCafe();

            // Yorum Sayısı Güncellemek
            long gercekYorumSayisi = reviewRepository.findByCafe_CafeId(cafe.getCafeId()).size();
            cafe.setReviewCount((int) gercekYorumSayisi);

            // Popüler Ürünler Analizi
            List<Object[]> topProducts = cafeRepository.findTopProductsByCafeId(cafe.getCafeId());
            List<String> popularItems = new ArrayList<>();
            for (Object[] row : topProducts) {
                String name = (String) row[0];
                Long count = ((Number) row[1]).longValue();
                popularItems.add(name + " (" + count + " kez)");
            }
            model.addAttribute("popularItems", popularItems);

            // Yoğun Zaman Analizi
            List<Object[]> busyDay = visitRepository.findBusiestDay(cafe.getCafeId());
            List<Object[]> busyHour = visitRepository.findBusiestHour(cafe.getCafeId());

            String busiestTime = "Veri Yok";
            if (!busyDay.isEmpty() && !busyHour.isEmpty()) {
                String day = (String) busyDay.get(0)[0];
                Integer hour = (Integer) busyHour.get(0)[0];
                busiestTime = translateDay(day) + " günü, saat " + hour + ":00 civarı";
            }
            model.addAttribute("busiestTime", busiestTime);

            model.addAttribute("cafe", cafe);
            return "admin-dashboard";
        } else {
            model.addAttribute("cafe", null);
            return "admin-dashboard";
        }
    }

    // YENİ KAFE EKLEME
    @GetMapping("/admin/add-cafe")
    public String showAddCafeForm() {
        return "add-cafe";
    }

    @PostMapping("/admin/add-cafe")
    public String saveCafe(@RequestParam String name,
            @RequestParam String city,
            @RequestParam String address,
            @RequestParam String licenseNumber,
            @RequestParam String phoneNumberRaw, // HTML'den gelen ham numara
            @RequestParam String openingHours,
            @RequestParam(defaultValue = "false") boolean hasDessert,
            HttpSession session, Model model) {

        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        // Ruhsat Kontrolü
        if (cafeRepository.findByLicenseNumber(licenseNumber) != null) {
            model.addAttribute("error", "Bu Ruhsat Numarası zaten kullanılıyor!");
            return "add-cafe";
        }

        // Telefon Formatlama (+90 Ekleme)
        String cleanNumber = phoneNumberRaw.replaceAll("\\s+", "");
        if (cleanNumber.length() != 10 || !cleanNumber.matches("\\d+")) {
            model.addAttribute("error",
                    "Lütfen telefon numarasını başında 0 olmadan, 10 hane olarak girin (Örn: 5321234567)");
            return "add-cafe";
        }
        String formattedPhone = "+90 " + cleanNumber;

        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).get();

        Cafe newCafe = new Cafe();
        newCafe.setName(name);
        newCafe.setCity(city);
        newCafe.setAddress(address);
        newCafe.setLicenseNumber(licenseNumber);
        newCafe.setPhoneNumber(formattedPhone);
        newCafe.setOpeningHours(openingHours);
        newCafe.setHasDessert(hasDessert);
        newCafe.setTotalRating(null);
        newCafe.setReviewCount(0);

        Cafe savedCafe = cafeRepository.save(newCafe);

        dbManager.setManagedCafe(savedCafe);
        employeeRepository.save(dbManager);

        return "redirect:/admin/dashboard";
    }

    // KAFE BİLGİLERİNİ DÜZENLEME
    @GetMapping("/admin/edit-cafe")
    public String showEditCafeForm(HttpSession session, Model model) {
        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).orElse(null);
        if (dbManager == null || dbManager.getManagedCafe() == null)
            return "redirect:/admin/add-cafe";

        model.addAttribute("cafe", dbManager.getManagedCafe());
        return "admin-edit-cafe";
    }

    @PostMapping("/admin/edit-cafe")
    public String updateCafe(@RequestParam Long cafeId,
            @RequestParam String name,
            @RequestParam String city,
            @RequestParam String address,
            @RequestParam String phoneNumber,
            @RequestParam String openingHours,
            @RequestParam String licenseNumber,
            @RequestParam(defaultValue = "false") boolean hasDessert,
            HttpSession session) {

        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).get();
        // Sadece kendi kafesini düzenleyebilir
        if (dbManager.getManagedCafe().getCafeId().equals(cafeId)) {

            Cafe cafe = cafeRepository.findById(cafeId).get();
            cafe.setName(name);
            cafe.setCity(city);
            cafe.setAddress(address);
            cafe.setPhoneNumber(phoneNumber);
            cafe.setOpeningHours(openingHours);
            cafe.setLicenseNumber(licenseNumber);
            cafe.setHasDessert(hasDessert);

            cafeRepository.save(cafe);
        }
        return "redirect:/admin/dashboard";
    }

    // MENÜ YÖNETİMİ

    @GetMapping("/admin/add-menu")
    public String showAddMenuForm(HttpSession session) {
        if (session.getAttribute("currentManager") == null)
            return "redirect:/";
        return "add-menu";
    }

    @PostMapping("/admin/add-menu")
    public String addMenuItem(@RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam String description,
            @RequestParam String category,
            HttpSession session) {
        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).get();
        Cafe cafe = dbManager.getManagedCafe();

        com.brewandreview.model.MenuItem item = new com.brewandreview.model.MenuItem();
        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);
        item.setCategory(category);

        menuItemRepository.save(item);
        cafe.getMenuItems().add(item);
        cafeRepository.save(cafe);

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/delete-menu")
    public String deleteMenuItem(@RequestParam Long menuId, HttpSession session) {
        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).get();
        Cafe cafe = dbManager.getManagedCafe();

        com.brewandreview.model.MenuItem itemToRemove = menuItemRepository.findById(menuId).orElse(null);

        if (itemToRemove != null && cafe.getMenuItems().contains(itemToRemove)) {
            cafe.getMenuItems().remove(itemToRemove);
            cafeRepository.save(cafe);
            menuItemRepository.delete(itemToRemove);
        }
        return "redirect:/admin/dashboard";
    }

    // PERSONEL YÖNETİMİ (EKLEME & SİLME)

    @GetMapping("/admin/add-staff")
    public String showAddStaffForm(HttpSession session) {
        if (session.getAttribute("currentManager") == null)
            return "redirect:/";
        return "add-staff";
    }

    @PostMapping("/admin/add-staff")
    public String addStaff(@RequestParam String name,
            @RequestParam Integer experience,
            @RequestParam String role,
            HttpSession session,
            Model model) {

        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        // Deneyim Yılı Kontrolü (>=0)
        if (experience < 0) {
            model.addAttribute("error", "Deneyim yılı negatif olamaz!");
            return "add-staff";
        }

        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).get();
        Cafe cafe = dbManager.getManagedCafe();

        //Employee Oluştur
        Employee newStaff = new Employee();
        newStaff.setName(name);
        newStaff.setExperienceYears(experience);
        newStaff.setRole(role);


        newStaff = employeeRepository.save(newStaff);

        //Eğer Baristaysa ona USER hesabı da aç (Otomatik)
        if ("barista".equals(role)) {
            // Kullanıcı adı üretimi
            String generatedUsername = name.toLowerCase().replaceAll("\\s+", "") + "_" + newStaff.getEmployeeId();
            newStaff.setUsername(generatedUsername);
            newStaff.setPasswordHash("1234"); // default şifre

            // User Tablosuna Ekle
            User newUser = new User();
            newUser.setUsername(generatedUsername);
            newUser.setEmail("barista" + newStaff.getEmployeeId() + "@brewreview.com");
            newUser.setPasswordHash("1234");
            newUser.setUserTitle("Barista ☕️");
            newUser.setSignupDate(Timestamp.from(Instant.now()));
            userRepository.save(newUser);

            // Employee'yi güncelle
            employeeRepository.save(newStaff);
        }

        cafe.getEmployees().add(newStaff);
        cafeRepository.save(cafe);

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/delete-staff")
    public String deleteStaff(@RequestParam Long staffId, HttpSession session) {
        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).get();
        Cafe cafe = dbManager.getManagedCafe();

        Employee staffToRemove = employeeRepository.findById(staffId).orElse(null);

        if (staffToRemove != null && cafe.getEmployees().contains(staffToRemove)) {
            cafe.getEmployees().remove(staffToRemove);
            cafeRepository.save(cafe);

            employeeRepository.delete(staffToRemove);
        }
        return "redirect:/admin/dashboard";
    }
}