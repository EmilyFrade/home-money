package com.homemoney.controllers.residence;

import com.homemoney.model.residence.Residence;
import com.homemoney.model.user.User;
import com.homemoney.services.residence.ResidenceService;
import com.homemoney.services.user.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/residence")
public class ResidenceController {

    @Autowired
    private ResidenceService residenceService;

    @Autowired
    private UserService userService;

    @GetMapping("/choose")
    public String chooseResidence(Model model) {
        model.addAttribute("residences", residenceService.findAll());
        return "residence/choose";
    }

    @PostMapping("/join")
    public String joinResidence(@RequestParam Long residenceId, Authentication authentication) {
        Residence residence = residenceService.findById(residenceId);
        Optional<User> user = userService.findCurrentUserByUsername(authentication); 
        userService.updateResidence(user.get().getId(), residence);
        return "redirect:/";
    }

    @PostMapping("/create")
    public String createAndJoinResidence(Residence residence, Authentication authentication) {
        residenceService.save(residence);
        Optional<User> user = userService.findCurrentUserByUsername(authentication); 
        userService.updateResidence(user.get().getId(), residence);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Residence residence = residenceService.findById(id);
        model.addAttribute("residence", residence);

        return "residence/form";
    }

    @PostMapping("/edit/{id}")
    public String updateResidence(@PathVariable Long id, @ModelAttribute Residence residence) {
        residence.setId(id);
        residenceService.save(residence);

        return "redirect:/residence";
    }

    @GetMapping("/delete/{id}")
    public String deleteResidence(@PathVariable Long id) {
        residenceService.delete(id);

        return "redirect:/residence";
    }
}