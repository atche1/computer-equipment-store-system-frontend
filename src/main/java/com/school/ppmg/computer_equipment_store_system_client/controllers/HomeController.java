package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductClient productClient;

    @GetMapping("/")
    public String home(Model model) {

        var productsPage = productClient.getAll(
                null, null, true,
                null, null, null,
                null, null, null, null,
                0, 12, "id,desc"
        );

        model.addAttribute("productsPage", productsPage);
        return "home";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}