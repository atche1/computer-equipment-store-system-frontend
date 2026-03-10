package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.OrderClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.order.UpdateOrderStatusRequest;
import com.school.ppmg.computer_equipment_store_system_client.enums.OrderStatus;
import com.school.ppmg.computer_equipment_store_system_client.security.AdminGuard;
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
    private final AdminGuard adminGuard;

    @GetMapping
    public String allOrders(@RequestParam(defaultValue = "0") int page,
                            HttpSession session,
                            Model model) {

        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        var ordersPage = orderClient.getAllOrders(page, 20, "createdAt,desc");
        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("statuses", OrderStatus.values());

        return "admin/orders/orders";
    }

    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id,
                               HttpSession session,
                               Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        model.addAttribute("order", orderClient.getMyOrderById(id));
        model.addAttribute("statuses", OrderStatus.values());

        return "admin/orders/order-details";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               HttpSession session) {

        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        orderClient.updateStatus(id, new UpdateOrderStatusRequest(status));
        return "redirect:/admin/orders";
    }
}