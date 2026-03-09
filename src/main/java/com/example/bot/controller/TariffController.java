package com.example.bot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bot.Dto.TariffRequest;
import com.example.bot.entity.Tariff;
import com.example.bot.entity.User;
import com.example.bot.service.TariffService;
import com.example.bot.service.UserService;



@Controller
@RequestMapping("/admin")
public class TariffController {
    
    private final TariffService tariffService;
    private final UserService userService;

    public TariffController(TariffService tariffService, UserService userService) {
        this.userService = userService;
        this.tariffService = tariffService;
    }

    /*
    @GetMapping("/tariffs")
    public String tariffsOverview() {
    // Отображает общее описание тарифов (без привязки к пользователю)
        return "tariffi"; // или redirect
    }
    */


    @GetMapping("/tariffs-admin")
    public String manageTariffs(Model model) {
        model.addAttribute("tariffs", tariffService.findAll());
        return "tariffs-admin";
    }

    @PostMapping("/tariffs")
    public String createTariff(TariffRequest request) {
        Tariff tariff = new Tariff();
        tariff.setTitle(request.getName());
        tariff.setPrice(request.getPrice());
        tariff.setDurationDays(request.getDurationDays());
        tariff.setActive(request.getIsActive());
        tariffService.save(tariff);
        return "redirect:/admin/tariffs-admin";
    }

    @PostMapping("/tariffs/{id}/toggle")
    public String toggleActive(@PathVariable Long id) {
        tariffService.toggleActive(id);
        return "redirect:/admin/tariffs-admin";
    }

    @PostMapping("/tariffs/{id}/delete")
    public String deleteTariff(@PathVariable Long id) {
        tariffService.deleteById(id);
        return "redirect:/admin/tariffs-admin";
    }
   
    /*@GetMapping("/tariffs/{id}")
    public String listTariffs(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "tariffi";
    }*/


    
}
