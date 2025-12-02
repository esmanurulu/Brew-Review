package com.brewandreview.controller;

import com.brewandreview.model.User;
import com.brewandreview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.Instant;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    //kayit formu
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    //kayit islemi
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(defaultValue = "false") boolean isBarista, // Barista mı?
            Model model) {

        //Kullanıcı adı kontrolü
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Bu kullanıcı adı zaten alınmış!");
            return "register";
        }

        //Yeni Kullanıcı Oluştur
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(password);
        newUser.setSignupDate(Timestamp.from(Instant.now()));

        // Barista seçimini işle
        if (isBarista) {
            newUser.setUserTitle("Barista ☕️");
        } else {
            newUser.setUserTitle("Kahve Sever");
        }

        userRepository.save(newUser);

        return "redirect:/?success=true";
    }
}