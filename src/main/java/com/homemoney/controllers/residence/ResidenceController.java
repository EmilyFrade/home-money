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

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/residence")
public class ResidenceController {

    @Autowired
    private ResidenceService residenceService;

    @Autowired
    private UserService userService;

    @GetMapping("/create")
    public String createResidenceForm(Model model) {
        model.addAttribute("residence", new Residence());
        return "residence/form";
    }

    @PostMapping("/create")
    public String createResidence(@ModelAttribute Residence residence, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userService.findCurrentUserByUsername(authentication)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));


        String inviteCode = UUID.randomUUID().toString().substring(0, 8);
        residence.setInviteCode(inviteCode);

        residenceService.save(residence);

        user.setResidence(residence);
        userService.save(user);

        redirectAttributes.addFlashAttribute("inviteCode", inviteCode);


        return "redirect:/residence/details";
    }

    @PostMapping("/join")
    public String joinResidence(@RequestParam String inviteCode, Authentication authentication, Model model) {

        Residence residence = residenceService.findByInviteCode(inviteCode).orElse(null);

        if (residence != null) {
            User user = userService.findCurrentUserByUsername(authentication)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            user.setResidence(residence);
            userService.save(user);

            model.addAttribute("residence", residence);
            return "residence/details";
        } else {
            model.addAttribute("invalidInviteCode", true);
            model.addAttribute("noResidence", true);
            return "home";
        }
    }

    @GetMapping("/details")
    public String showResidenceDetails(Model model, Authentication authentication) {
        User user = userService.findCurrentUserByUsername(authentication)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Residence residence = user.getResidence();
        List<User> residenceUsers = userService.findByResidence(residence.getId());

        model.addAttribute("residence", residence);
        model.addAttribute("residenceUsers", residenceUsers);

        return "residence/details";
    }
}