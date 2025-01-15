package com.homemoney.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.homemoney.model.user.PasswordResetToken;
import com.homemoney.model.user.User;
import com.homemoney.repositories.user.UserRepository;
import com.homemoney.services.user.PasswordResetTokenService;
import com.homemoney.services.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/password")
public class PasswordResetController {

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/forgot")
    public String showForgotPasswordPage() {
        return "user/forgotPassword";
    }

    @PostMapping("/forgot")
    public String handleForgotPassword(@RequestParam String username, Model model) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Usuário não encontrado.");
            return "user/forgotPassword";
        }

        String token = passwordResetTokenService.createToken(user);
        passwordResetTokenService.sendPasswordResetEmail(user, token);

        model.addAttribute("message", "Um link de redefinição foi enviado para seu e-mail.");
        return "user/forgotPassword";
    }

    @GetMapping("/reset")
    public String showResetPasswordPage(@RequestParam String token, Model model) {
        if (!passwordResetTokenService.validateToken(token)) {
            model.addAttribute("error", "Token inválido ou expirado.");
            return "user/forgotPassword";
        }

        model.addAttribute("token", token);
        return "user/resetPassword"; 
    }

    @PostMapping("/reset")
    public String handleResetPassword(@RequestParam String token, @RequestParam String newPassword, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!passwordResetTokenService.validateToken(token)) {
            redirectAttributes.addFlashAttribute("error", "Token inválido ou expirado.");
            return "user/forgotPassword";
        }

        PasswordResetToken resetToken = passwordResetTokenService.getToken(token).orElseThrow(() -> new IllegalArgumentException("Token não encontrado."));

        User user = resetToken.getUser();
        user.setPassword(newPassword);
        userService.saveAndAuthenticate(user, request);

        passwordResetTokenService.markTokenAsUsed(resetToken);

        redirectAttributes.addFlashAttribute("message", "Senha redefinida com sucesso.");
        return "redirect:/";
    }
}