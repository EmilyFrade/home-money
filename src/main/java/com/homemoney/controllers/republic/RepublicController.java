package com.homemoney.controllers.republic;

import com.homemoney.model.republic.Republic;
import com.homemoney.services.republic.RepublicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/republic")
public class RepublicController {

    @Autowired
    private RepublicService republicService;

    // Exibe o formulário de cadastro de república
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("republica", new Republic());
        return "republic/form";
    }

    // Salva a república após a validação do formulário
    @PostMapping("/create")
    public String saveRepublica(@Valid @ModelAttribute Republic republic, BindingResult result) {
        if (result.hasErrors()) {
            return "republic/form"; // Retorna ao formulário em caso de erro
        }

        republicService.save(republic); // Salva a república com os dados fornecidos
        return "redirect:/republic"; // Redireciona para a lista de repúblicas após o cadastro
    }

    // Exibe o formulário de edição da república
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") long id, Model model) {
        Republic republic = republicService.findById(id);
        model.addAttribute("republica", republic);
        return "republic/form"; // Exibe o formulário com os dados da república para edição
    }

    // Atualiza a república após a edição
    @PostMapping("/edit/{id}")
    public String updateRepublica(@PathVariable("id") long id, @Valid @ModelAttribute Republic republic, BindingResult result) {
        if (result.hasErrors()) {
            return "republic/form"; // Retorna ao formulário em caso de erro
        }

        republic.setId(id); // Define o ID da república
        republicService.save(republic); // Atualiza a república
        return "redirect:/republic"; // Redireciona para a lista de repúblicas após a edição
    }

    // Exibe a lista de repúblicas
    @GetMapping("")
    public String listRepublicas(Model model) {
        model.addAttribute("republicas", republicService.findAll());
        return "republic/list"; // Retorna para uma página de listagem de repúblicas
    }
}
