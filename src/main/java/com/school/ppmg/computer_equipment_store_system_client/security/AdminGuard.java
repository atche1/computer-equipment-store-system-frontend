package com.school.ppmg.computer_equipment_store_system_client.security;

import com.school.ppmg.computer_equipment_store_system_client.controllers.AuthController;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class AdminGuard {

    public boolean isAdmin(HttpSession session) {
        return session != null
                && "ADMIN".equals(session.getAttribute(AuthController.SESSION_ROLE));
    }

    public String check(HttpSession session) {
        return isAdmin(session) ? null : "redirect:/access-denied";
    }
}