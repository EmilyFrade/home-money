package com.homemoney.controllers;

import com.homemoney.model.user.User;
import com.homemoney.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String homePage(Authentication authentication, Model model) {
        User user = userService.findCurrentUserByUsername(authentication);

        // Adicionar usuário ao modelo
        model.addAttribute("user", user);

        // Adicionar residência ao modelo
        if (user.getResidence() == null) {

            model.addAttribute("noResidence", true);
        } else {

            model.addAttribute("residence", user.getResidence());
        }

        return "home";
    }
}
