package com.homemoney.controllers.residence;

import com.homemoney.model.residence.Residence;
import com.homemoney.model.user.User;
import com.homemoney.services.residence.ResidenceService;
import com.homemoney.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/residence")
public class ResidenceController {

    @Autowired
    private ResidenceService residenceService;

    @Autowired
    private UserService userService;

    // Rota para exibir o formulário de criação de residência
    @GetMapping("/create")
    public String createResidenceForm(Model model) {
        model.addAttribute("residence", new Residence());
        return "residence/form"; // Aqui vai para o formulário de criação da residência
    }

    // Rota para salvar a residência criada
    @PostMapping("/create")
    public String createResidence(@ModelAttribute Residence residence, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Gera o código de convite para a residência
        String inviteCode = UUID.randomUUID().toString().substring(0, 8); // Gera um código único
        residence.setInviteCode(inviteCode); // Associa o código à residência

        // Salva a residência
        residenceService.save(residence);

        // Associa a residência ao usuário
        user.setResidence(residence);
        userService.save(user);

        // Adiciona o código de convite à resposta de redirecionamento
        redirectAttributes.addFlashAttribute("inviteCode", inviteCode);

        // Redireciona para a página da residência
        return "redirect:/residence/details"; // A página para exibir os detalhes da residência
    }

    // Rota para entrada com código de convite
    @PostMapping("/join")
    public String joinResidence(@RequestParam String inviteCode, Authentication authentication, Model model) {
        // Verifica se a residência com o código de convite existe
        Residence residence = residenceService.findByInviteCode(inviteCode).orElse(null);

        if (residence != null) {
            // Se o código for válido, associa a residência ao usuário
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            user.setResidence(residence);
            userService.save(user);

            // Redireciona para a página da residência
            model.addAttribute("residence", residence);
            return "residence/details";  // Página que mostra a residência do usuário
        } else {
            // Se o código for inválido, mostra a mensagem de erro na mesma página
            model.addAttribute("invalidInviteCode", true);
            model.addAttribute("noResidence", true); // Para garantir que a mensagem de convite apareça
            return "home";  // Retorna para a página inicial, mostrando o erro
        }
    }

    // Rota para exibir os detalhes da residência
    @GetMapping("/details")
    public String showResidenceDetails(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Se o usuário tiver uma residência, exibe os detalhes
        model.addAttribute("residence", user.getResidence());

        // Se um código de convite foi gerado e está disponível, passá-lo também para o modelo
        if (model.containsAttribute("inviteCode")) {
            model.addAttribute("inviteCode", model.getAttribute("inviteCode"));
        }

        return "residence/details"; // Página para exibir os detalhes da residência
    }
}
