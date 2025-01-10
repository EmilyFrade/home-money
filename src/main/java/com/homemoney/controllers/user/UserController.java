package com.homemoney.controllers.user;

import com.homemoney.model.user.User;
import com.homemoney.services.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    // Exibe o formulário de cadastro de usuário
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user/form";
    }

    // Salva o usuário após a validação do formulário
    @PostMapping
    public String saveUser(@Valid @ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "user/form";
        }

        userService.save(user);
        return "redirect:/user";
    }

    // Exibe o formulário de edição de usuário
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        if (user != null) {
            model.addAttribute("user", user);
            return "user/form";
        }
        return "redirect:/user";
    }

    // Atualiza os dados do usuário após a validação do formulário
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "user/form";
        }
        user.setId(id);
        userService.save(user);
        return "redirect:/user";
    }

    // Deleta o usuário
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/user";
    }
}
