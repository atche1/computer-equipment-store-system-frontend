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
    public String adminOrders(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "createdAt,desc") String sort,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String orderNumber,
                              @RequestParam(required = false) String customerName,
                              @RequestParam(required = false) String dateFrom,
                              @RequestParam(required = false) String dateTo,
                              HttpSession session,
                              Model model) {

        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        var ordersPage = orderClient.getAllOrders(
                page, size, sort, status, orderNumber, customerName, dateFrom, dateTo
        );

        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("customerName", customerName);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        return "admin/orders/orders";
    }

    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id,
                               HttpSession session,
                               Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        model.addAttribute("order", orderClient.getOrderById(id));
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
