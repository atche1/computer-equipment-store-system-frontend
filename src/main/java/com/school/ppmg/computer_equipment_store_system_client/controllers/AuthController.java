package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.AuthClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.CartClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.cart.MergeCartRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.security.AuthResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.security.LoginRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.security.RegisterRequest;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.BackendException;
import com.school.ppmg.computer_equipment_store_system_client.session.SessionCart;
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
    public static final String SESSION_CART = "SESSION_CART";

    private final CartClient cartClient;
    private final AuthClient authClient;

    private boolean isLogged(HttpSession session) {
        return session.getAttribute(SESSION_ACCESS_TOKEN) != null;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String returnUrl,
                            HttpSession session,
                            Model model) {

        if (isLogged(session)) {
            if (returnUrl != null && !returnUrl.isBlank() && returnUrl.startsWith("/")) {
                return "redirect:" + returnUrl;
            }
            return "redirect:/";
        }

        model.addAttribute("returnUrl", returnUrl);
        return "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          @RequestParam(required = false) String returnUrl,
                          HttpSession session,
                          Model model) {

        try {
            LoginRequest req = new LoginRequest();
            req.setEmail(email);
            req.setPassword(password);

            AuthResponse res = authClient.login(req);

            session.setAttribute(SESSION_ACCESS_TOKEN, res.accessToken());
            session.setAttribute(SESSION_ROLE, res.role());
            session.setAttribute(SESSION_EMAIL, email);

            SessionCart guestCart = (SessionCart) session.getAttribute(SESSION_CART);
            if (guestCart != null && !guestCart.isEmpty()) {
                cartClient.mergeCart(new MergeCartRequest(guestCart.getItems()));
                session.removeAttribute(SESSION_CART);
            }

            if (returnUrl != null && !returnUrl.isBlank() && returnUrl.startsWith("/")) {
                return "redirect:" + returnUrl;
            }

            return "redirect:/";

        } catch (Exception ex) {
            model.addAttribute("error", "Invalid email or password.");
            model.addAttribute("returnUrl", returnUrl);
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {

        if (isLogged(session)) {
            return "redirect:/";
        }

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
            RegisterRequest req = new RegisterRequest();
            req.setEmail(email);
            req.setPassword(password);
            req.setFirstName(firstName);
            req.setLastName(lastName);
            req.setPhone(phone);

            authClient.register(req);

            return "redirect:/login";

        } catch (BackendException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("fieldErrors", ex.getFieldErrors());
            return "auth/register";

        } catch (Exception ex) {
            model.addAttribute("error", "Registration failed. Please try again.");
            return "auth/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}