package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.AdminDashboardClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardClient dashboardClient;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        try {
            var data = dashboardClient.getDashboard();
            model.addAttribute("data", data);
        } catch (Exception e) {
            model.addAttribute("error", "Cannot load dashboard");
        }

        return "admin/dashboard";
    }
}