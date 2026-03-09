package com.example.bot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bot.entity.AiModel;
import com.example.bot.repository.AiModelRepository;
import com.example.bot.service.AiModelService;

@Controller
@RequestMapping("/admin")
public class AiModelController {

    private final AiModelService aiModelService;

    public AiModelController(AiModelService service){
        aiModelService = service;
    }

    @GetMapping("/models")
    public String models(Model model) {
        model.addAttribute("models", aiModelService.getAll());
        model.addAttribute("newModel", new AiModel());
        return "aimodels";
    }
    
}
