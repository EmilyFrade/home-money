package com.homemoney.controllers;

import com.homemoney.services.user.UserService;
import com.homemoney.model.user.User;
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
        String username = authentication.getName(); // Obtém o nome de usuário do usuário logado
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Verificamos se o usuário possui uma residência
        if (user.getResidence() == null) {
            // Usuário não tem residência associada
            model.addAttribute("noResidence", true); // Indicamos que o usuário não tem residência
        } else {
            // Usuário tem residência associada
            model.addAttribute("residence", user.getResidence()); // Passamos a residência do usuário
        }

        return "home"; // Retorna a página inicial
    }
}
