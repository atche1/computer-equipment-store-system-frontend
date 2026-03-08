package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.OrderClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.order.UpdateOrderStatusRequest;
import com.school.ppmg.computer_equipment_store_system_client.enums.OrderStatus;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderClient orderClient;

    @GetMapping
    public String allOrders(@RequestParam(defaultValue = "0") int page,
                            HttpSession session,
                            Model model) {

        if (!"ADMIN".equals(session.getAttribute(AuthController.SESSION_ROLE))) {
            return "redirect:/";
        }

        var ordersPage = orderClient.getAllOrders(page, 20, "createdAt,desc");
        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               HttpSession session) {

        if (!"ADMIN".equals(session.getAttribute(AuthController.SESSION_ROLE))) {
            return "redirect:/";
        }

        orderClient.updateStatus(id, new UpdateOrderStatusRequest(status));
        return "redirect:/admin/orders";
    }
}