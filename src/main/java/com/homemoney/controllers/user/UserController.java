package com.homemoney.controllers.user;

import com.homemoney.model.user.User;
import com.homemoney.services.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // Exibe tela de perfil do usuário
    @GetMapping
    public String showProfile(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            if (user.getResidence() != null) {
                // Usuário já está associado a uma residência
                model.addAttribute("residence", user.getResidence());
            }
            
            return "user/profile";
        }

        return "redirect:/login";
    }

    // Exibe a página para escolher ou criar uma residência
    @GetMapping("/chooseResidence")
    public String chooseResidencePage(Model model) {
        model.addAttribute("user", new User()); // Apenas para exibição, não precisa de um novo usuário aqui
        return "user/chooseResidence"; // Página para o usuário decidir o que fazer
    }

    // Exibe o formulário de cadastro de usuário
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user/createForm";
    }

    // Salva o usuário
    @PostMapping("/create/{id}")
    public String createUser(@PathVariable Long id, @ModelAttribute User user, HttpServletRequest request) {
        user.setId(id);
        userService.saveAndAuthenticate(user, request);
        return "redirect:/";
    }

    // Exibe o formulário de edição de usuário
    @GetMapping("/edit/{id}")
    public String showEditForm(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "user/editForm";
        }
        return "redirect:/user";
    }

    // Edita o usuário
    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, @ModelAttribute User user, HttpServletRequest request) {
        userService.update(id, user);
        return "redirect:/user";
    }
}