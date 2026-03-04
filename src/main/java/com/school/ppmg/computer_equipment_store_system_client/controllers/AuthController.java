package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.AuthClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.security.AuthResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.security.LoginRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.security.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    public static final String SESSION_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String SESSION_ROLE = "ROLE";
    public static final String SESSION_EMAIL = "EMAIL";

    private final AuthClient authClient;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        try {
            // DTO is CLASS -> build it safely (works with setters)
            LoginRequest req = new LoginRequest();
            req.setEmail(email);
            req.setPassword(password);

            // Response is RECORD -> access via res.accessToken(), res.role()
            AuthResponse res = authClient.login(req);

            session.setAttribute(SESSION_ACCESS_TOKEN, res.accessToken());
            session.setAttribute(SESSION_ROLE, res.role());
            session.setAttribute(SESSION_EMAIL, email);

            return "redirect:/";
        } catch (Exception ex) {
            model.addAttribute("error", "Грешен имейл или парола");
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String phone,
                             Model model) {
        try {
            // DTO is CLASS -> build it safely (works with setters)
            RegisterRequest req = new RegisterRequest();
            req.setEmail(email);
            req.setPassword(password);
            req.setFirstName(firstName);
            req.setLastName(lastName);
            req.setPhone(phone);

            authClient.register(req);
            return "redirect:/login";
        } catch (Exception ex) {
            model.addAttribute("error", "Регистрацията не успя: " + ex.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}