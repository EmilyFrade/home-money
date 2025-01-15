package com.homemoney.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.homemoney.model.user.PasswordResetToken;
import com.homemoney.model.user.User;
import com.homemoney.repositories.user.PasswordResetTokenRepository;
import com.homemoney.services.email.EmailService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${base.url}")
    private String baseUrl;

    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token); // TO DO: criptografar o token antes de salvar no banco
        resetToken.setUser(user);
        resetToken.setExpiration(LocalDateTime.now().plusMinutes(5));
        passwordResetTokenRepository.save(resetToken);
        
        return token;
    }

    public void sendPasswordResetEmail(User user, String token) {
        String resetLink = baseUrl + "/password/reset?token=" + token;
        emailService.sendEmail(user.getUsername(), "Redefinição de Senha", "Clique no link para redefinir sua senha: " + resetLink); // TO DO: esconder esse link no e-mail em um botão
    }

    public Boolean validateToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token).orElse(null);
        if (resetToken == null || resetToken.getUsed() || resetToken.getExpiration().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    public Optional<PasswordResetToken> getToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public void markTokenAsUsed(PasswordResetToken resetToken) {
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}
