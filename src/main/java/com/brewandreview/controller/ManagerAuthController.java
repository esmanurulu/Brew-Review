package com.brewandreview.controller;

import com.brewandreview.model.Employee;
import com.brewandreview.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManagerAuthController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/manager/register")
    public String showManagerRegisterForm() {
        return "manager-register";
    }

    @PostMapping("/manager/register")
    public String registerManager(@RequestParam String name,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String citizenId, // TC Kimlik
            Model model) {

        // 1. TC Kimlik Kontrolü (11 Haneli mi?)
        if (citizenId.length() != 11 || !citizenId.matches("\\d+")) {
            model.addAttribute("error", "TC Kimlik / Sicil No 11 haneli olmalıdır!");
            return "manager-register";
        }

        // 2. Kullanıcı Adı Kontrolü
        if (employeeRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Bu kullanıcı adı zaten kullanımda.");
            return "manager-register";
        }

        // 3. Yeni Yönetici Oluştur
        Employee newManager = new Employee();
        newManager.setName(name);
        newManager.setUsername(username);
        newManager.setPasswordHash(password);
        newManager.setRole("manager");
        newManager.setExperienceYears(0);
        newManager.setCitizenId(citizenId);

        employeeRepository.save(newManager);

        return "redirect:/?manager_registered=true";
    }
}