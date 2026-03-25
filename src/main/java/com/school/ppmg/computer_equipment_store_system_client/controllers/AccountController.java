package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.UserClient;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final UserClient userClient;

    @GetMapping("/my-account")
    public String myAccount(HttpSession session, Model model) {
        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        try {
            model.addAttribute("account", userClient.getMyAccount());
            return "account/my-account";
        } catch (Exception ex) {
            model.addAttribute("error", "Unable to load account information.");
            return "account/my-account";
        }
    }
}