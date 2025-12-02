package com.brewandreview.controller;

import com.brewandreview.model.Cafe;
import com.brewandreview.model.Employee;
import com.brewandreview.repository.CafeRepository;
import com.brewandreview.repository.EmployeeRepository;
import com.brewandreview.repository.MenuItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;

    // --- DASHBOARD ---
    @Autowired
    private com.brewandreview.repository.ReviewRepository reviewRepository; // Bunu en üste ekle

    // YÖNETİCİ PANELİ
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        // Yönettiği kafeyi taze çek
        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).orElse(null);

        if (dbManager != null && dbManager.getManagedCafe() != null) {
            Cafe cafe = dbManager.getManagedCafe();

            long gercekYorumSayisi = reviewRepository.findByCafe_CafeId(cafe.getCafeId()).size();
            cafe.setReviewCount((int) gercekYorumSayisi);
            // (Sadece ekranda göstermek için set ettik, veritabanına kaydetmeye gerek yok)
            // -----------------------------------------------------------------------

            model.addAttribute("cafe", cafe);
            return "admin-dashboard";
        } else {
            model.addAttribute("cafe", null);
            return "admin-dashboard";
        }
    }

    // --- KAFE EKLEME ---
    @GetMapping("/admin/add-cafe")
    public String showAddCafeForm() {
        return "add-cafe";
    }

    @PostMapping("/admin/add-cafe")
    public String saveCafe(@RequestParam String name,
            @RequestParam String city,
            @RequestParam String address,
            @RequestParam String licenseNumber,
            @RequestParam String phoneNumberRaw, // HTML'den gelen ham numara (5XX...)
            @RequestParam String openingHours,
            @RequestParam(defaultValue = "false") boolean hasDessert,
            HttpSession session, Model model) {

        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        // 1. RUHSAT KONTROLÜ
        if (cafeRepository.findByLicenseNumber(licenseNumber) != null) {
            model.addAttribute("error", "Bu Ruhsat Numarası zaten kullanılıyor!");
            return "add-cafe";
        }

        // 2. TELEFON FORMATLAMA (Kafeler İçin)
        // Boşlukları temizle
        String cleanNumber = phoneNumberRaw.replaceAll("\\s+", "");
        // 10 hane kontrolü (Başında 0 olmadan)
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
        newCafe.setPhoneNumber(formattedPhone); // Formatlı no
        newCafe.setOpeningHours(openingHours);
        newCafe.setHasDessert(hasDessert);
        newCafe.setTotalRating(null);
        newCafe.setReviewCount(0);

        Cafe savedCafe = cafeRepository.save(newCafe);

        dbManager.setManagedCafe(savedCafe);
        employeeRepository.save(dbManager);

        return "redirect:/admin/dashboard";
    }

    // --- KAFE DÜZENLEME ---
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

    // --- MENÜ EKLEME ---
    @GetMapping("/admin/add-menu")
    public String showAddMenuForm(HttpSession session) {
        if (session.getAttribute("currentManager") == null)
            return "redirect:/";
        return "add-menu";
    }

    @PostMapping("/admin/add-menu")
    public String addMenuItem(@RequestParam String name,
            @RequestParam java.math.BigDecimal price,
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

    // --- PERSONEL EKLEME (GÜNCELLENDİ: SADECE DENEYİM KONTROLÜ) ---
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
            Model model) { // Model eklendi

        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        // DENEYİM KONTROLÜ
        if (experience < 0) {
            model.addAttribute("error", "Deneyim yılı negatif olamaz!");
            return "add-staff";
        }

        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).get();
        Cafe cafe = dbManager.getManagedCafe();

        Employee newStaff = new Employee();
        newStaff.setName(name);
        newStaff.setExperienceYears(experience);
        newStaff.setRole(role);

        employeeRepository.save(newStaff);
        cafe.getEmployees().add(newStaff);
        cafeRepository.save(cafe);

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/delete-menu")
    public String deleteMenuItem(@RequestParam Long menuId, HttpSession session) {
        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        // Güvenlik: Silinecek ürün gerçekten bu yöneticinin kafesine mi ait?
        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).get();
        Cafe cafe = dbManager.getManagedCafe();

        // Ürünü bul
        com.brewandreview.model.MenuItem itemToRemove = menuItemRepository.findById(menuId).orElse(null);

        if (itemToRemove != null && cafe.getMenuItems().contains(itemToRemove)) {
            // İlişkiyi kes
            cafe.getMenuItems().remove(itemToRemove);
            cafeRepository.save(cafe);

            // Ürünü tamamen sil
            menuItemRepository.delete(itemToRemove);
        }

        return "redirect:/admin/dashboard";
    }

    // 2. Personel Sil (İşten Çıkar)
    @PostMapping("/admin/delete-staff")
    public String deleteStaff(@RequestParam Long staffId, HttpSession session) {
        Employee manager = (Employee) session.getAttribute("currentManager");
        if (manager == null)
            return "redirect:/";

        Employee dbManager = employeeRepository.findById(manager.getEmployeeId()).get();
        Cafe cafe = dbManager.getManagedCafe();

        Employee staffToRemove = employeeRepository.findById(staffId).orElse(null);

        if (staffToRemove != null && cafe.getEmployees().contains(staffToRemove)) {
            // İlişkiyi kes
            cafe.getEmployees().remove(staffToRemove);
            cafeRepository.save(cafe);

            // Personeli sil
            employeeRepository.delete(staffToRemove);
        }

        return "redirect:/admin/dashboard";
    }
}