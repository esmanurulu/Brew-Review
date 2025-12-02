package com.brewandreview;

import com.brewandreview.model.Employee;
import com.brewandreview.model.User;
import com.brewandreview.repository.EmployeeRepository;
import com.brewandreview.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(name = "user", required = false, defaultValue = "Misafir") String user,
            Model model) {
        model.addAttribute("user", user);
        return "dashboard";
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, String> login(@RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(name = "type", defaultValue = "user") String type,
            HttpSession session) {

        Map<String, String> response = new HashMap<>();

        if ("admin".equals(type)) {
            // YÖNETİCİ GİRİŞİ
            Employee emp = employeeRepository.findByUsername(username);

            if (emp != null && emp.getPasswordHash().trim().equals(password.trim())
                    && "manager".equals(emp.getRole())) {
                session.setAttribute("currentManager", emp);
                response.put("status", "success");
                response.put("redirect", "/admin/dashboard");
            } else {
                response.put("status", "error");
                response.put("message", "Hatalı yönetici bilgileri!");
            }
        } else {
            // NORMAL KULLANICI GİRİŞİ
            User dbUser = userRepository.findByUsername(username);
            if (dbUser != null && dbUser.getPasswordHash().trim().equals(password.trim())) {
                session.setAttribute("currentUser", dbUser);
                response.put("status", "success");
                response.put("redirect", "/dashboard?user=" + dbUser.getUsername());
            } else {
                response.put("status", "error");
                response.put("message", "Hatalı kullanıcı adı veya şifre!");
            }
        }

        return response;
    }

    // --- GÜVENLİ ÇIKIŞ ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Tüm oturum bilgilerini sil
        return "redirect:/"; // Ana sayfaya tertemiz gönder
    }
}