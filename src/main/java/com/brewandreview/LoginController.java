package com.brewandreview;

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

    // Mock Kullanıcı
    private final String MOCK_USER = "kahvesever";
    private final String MOCK_PASS = "1234";

    // Ana Sayfa (index.html)
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Dashboard Sayfası
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(name = "user", required = false, defaultValue = "Misafir") String user,
            Model model) {
        model.addAttribute("user", user);
        return "dashboard";
    }

    // Giriş - json
    @PostMapping("/login")
    @ResponseBody
    public Map<String, String> login(@RequestParam("username") String username,
            @RequestParam("password") String password) {

        Map<String, String> response = new HashMap<>();

        if (MOCK_USER.equals(username) && MOCK_PASS.equals(password)) {
            response.put("status", "success");
            response.put("message", "Giriş Başarılı! Java Backend Çalışıyor...");
        } else {
            response.put("status", "error");
            response.put("message", "Hatalı Şifre!");
        }

        return response;
    }
}