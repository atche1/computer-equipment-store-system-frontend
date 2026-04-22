package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.UserClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.user.MyAccountResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
            MyAccountResponse account = userClient.getMyAccount();

            boolean isAdmin = "ADMIN".equals(account.role());
            model.addAttribute("account", account);
            model.addAttribute("isAdmin", isAdmin);

            return "account/my-account";
        } catch (Exception ex) {
            model.addAttribute("error", "Unable to load account information.");
            return "account/my-account";
        }
    }
}