package com.luanpereira.semcitecsystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/institute")
public class InstituteController {
    @GetMapping
    public String institute(Model model) {
        model.addAttribute("content", "institute");
        return "default";
    }
}
