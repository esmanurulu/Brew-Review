package com.brewandreview.controller;

import com.brewandreview.model.*;
import com.brewandreview.repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Timestamp;
import java.time.Instant;

@Controller
public class FollowController {

    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/follow/employee/{id}")
    @Transactional
    public String toggleFollowEmployee(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/";

        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            if (followRepository.existsByUserAndEmployee(currentUser, employee)) {
                // Zaten takip ediyorsa çıkar
                followRepository.deleteByUserAndEmployee(currentUser, employee);
            } else {
                // Takip etmiyorsa ekle
                Follow follow = new Follow();
                follow.setUser(currentUser);
                follow.setEmployee(employee);
                follow.setFollowDate(Timestamp.from(Instant.now()));
                followRepository.save(follow);
            }
            // İşlem bitince kafenin detay sayfasına geri dön (Baristanın çalıştığı kafe)
            // Not: Baristanın kafesi yoksa ana sayfaya dön
            if (employee.getManagedCafe() != null) { // Manager ise
                return "redirect:/cafe/" + employee.getManagedCafe().getCafeId();
            } else {
                // Normal baristaların kafe ilişkisini Employee_Cafe tablosundan bulmak
                return "redirect:/cafes";
            }
        }
        return "redirect:/cafes";
    }
}