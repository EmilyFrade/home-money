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

    @GetMapping
    public String showProfile(Authentication authentication, Model model) {
        Optional<User> userOpt = userService.findCurrentUserByUsername(authentication);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            if (user.getResidence() != null) {
                model.addAttribute("residence", user.getResidence());
            }
            
            return "user/profile";
        }

        return "redirect:/login";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user/createForm";
    }

    @PostMapping("/create/{id}")
    public String createUser(@PathVariable Long id, @ModelAttribute User user, HttpServletRequest request) {
        user.setId(id);
        userService.saveAndAuthenticate(user, request);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<User> userOpt = Optional.ofNullable(userService.findById(id));

        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "user/editForm";
        }
        return "redirect:/user";
    }

    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, @ModelAttribute User user, HttpServletRequest request) {
        userService.update(id, user);
        return "redirect:/user";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "/user/resetPassword";
    }
}