package com.amazing.credit.controller;

import com.amazing.credit.service.CreditLimits;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/conflict")
public class ConflictController {

    private final CreditLimits creditLimits;

    public ConflictController(CreditLimits creditLimits) {
        this.creditLimits = creditLimits;
    }

    @GetMapping()
    public String getConflict(Model model) {
        model.addAttribute("conflicts",  creditLimits.getCreditConflict());
        model.addAttribute("loans",  creditLimits.getAllWorkBook());
        return "conflicts.html";
    }
}
