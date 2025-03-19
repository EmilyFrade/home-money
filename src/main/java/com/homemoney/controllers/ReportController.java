package com.homemoney.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    @GetMapping("/report")
    public String showReportPage() {
        return "report"; // Nome do arquivo na pasta templates (sem .html)
    }
}
