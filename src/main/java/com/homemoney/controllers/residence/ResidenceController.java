package com.homemoney.controllers.residence;

import com.homemoney.model.residence.Residence;
import com.homemoney.services.residence.ResidenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/residence")
public class ResidenceController {

    @Autowired
    private ResidenceService residenceService;

    @GetMapping
    public String listResidences(Model model) {
        List<Residence> residences = residenceService.findAll();
        model.addAttribute("residences", residences);

        return "residence/confirm";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("residence", new Residence());

        return "residence/form";
    }

    @PostMapping
    public String saveResidence(@ModelAttribute Residence residence) {
        residenceService.save(residence);

        return "redirect:/residence/create";
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
