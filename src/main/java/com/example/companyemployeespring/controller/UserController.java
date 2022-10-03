package com.example.companyemployeespring.controller;

import com.example.companyemployeespring.entity.User;
import com.example.companyemployeespring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }
    @PostMapping("/register")
    public String register (@ModelAttribute User user,
                            ModelMap modelMap){
        Optional<User> byEmail = userRepository.findByEmail(user.getEmail());
        if (byEmail.isPresent()) {
            modelMap.addAttribute("errorMessage","Email already in use");
            return "register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/";
    }
}
