package com.brewandreview.controller;

import com.brewandreview.model.Cafe;
import com.brewandreview.model.User;
import com.brewandreview.model.Visit;
import com.brewandreview.repository.CafeRepository;
import com.brewandreview.repository.VisitRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class VisitController {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private CafeRepository cafeRepository;

    // Ziyaret Ekleme (Check-in) İşlemi
    @PostMapping("/cafe/{cafeId}/visit")
    public String addVisit(@PathVariable Long cafeId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 1. Kullanıcı Giriş Yapmış mı?
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/"; // Giriş yapmamışsa ana sayfaya at
        }

        // 2. Kafeyi Bul
        Cafe cafe = cafeRepository.findById(cafeId).orElse(null);
        if (cafe != null) {
            // 3. Ziyareti Oluştur
            Visit visit = new Visit();
            visit.setUser(currentUser);
            visit.setCafe(cafe);

            // Tarih ve Saati şu anki zaman olarak ayarla
            visit.setVisitDate(Date.valueOf(LocalDate.now()));
            visit.setVisitTime(Time.valueOf(LocalTime.now()));
            visit.setVisitNote("Hızlı Check-in"); // Varsayılan not

            visitRepository.save(visit);

            // Başarı mesajı (Sayfada göstermek için)
            redirectAttributes.addFlashAttribute("successMessage", "📍 Check-in Başarılı! Ziyaretin kaydedildi.");
        }

        return "redirect:/cafe/" + cafeId; // Detay sayfasına geri dön
    }

    @GetMapping("/my-visits") // import org.springframework.web.bind.annotation.GetMapping;
    public String showMyVisits(HttpSession session, org.springframework.ui.Model model) { // Model importuna dikkat

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/";
        }

        // Kullanıcının ID'sine göre ziyaretleri bul (Repository'de bu metodu
        // tanımlamıştık)
        // List<Visit> visits =
        // visitRepository.findByUser_UserId(currentUser.getUserId());

        model.addAttribute("visits", visitRepository.findByUser_UserId(currentUser.getUserId()));

        return "my-visits"; // my-visits.html sayfasına git
    }
}
