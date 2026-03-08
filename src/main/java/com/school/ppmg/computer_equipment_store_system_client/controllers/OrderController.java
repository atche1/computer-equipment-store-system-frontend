package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.OrderClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.order.CheckoutRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.order.OrderResponse;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.BackendException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderClient orderClient;

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        if (!model.containsAttribute("checkoutRequest")) {
            model.addAttribute("checkoutRequest", new CheckoutRequest());
        }

        if (!model.containsAttribute("fieldErrors")) {
            model.addAttribute("fieldErrors", null);
        }

        return "orders/checkout";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam String deliveryName,
                           @RequestParam String deliveryPhone,
                           @RequestParam String country,
                           @RequestParam String city,
                           @RequestParam String postalCode,
                           @RequestParam String street,
                           @RequestParam String streetNumber,
                           HttpSession session,
                           Model model) {

        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        CheckoutRequest request = new CheckoutRequest();
        request.setDeliveryName(deliveryName != null ? deliveryName.trim() : null);
        request.setDeliveryPhone(deliveryPhone != null ? deliveryPhone.trim() : null);
        request.setCountry(country != null ? country.trim() : null);
        request.setCity(city != null ? city.trim() : null);
        request.setPostalCode(postalCode != null ? postalCode.trim() : null);
        request.setStreet(street != null ? street.trim() : null);
        request.setStreetNumber(streetNumber != null ? streetNumber.trim() : null);

        try {
            OrderResponse order = orderClient.checkout(request);
            return "redirect:/orders/" + order.id();

        } catch (BackendException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("fieldErrors", ex.getFieldErrors());
            model.addAttribute("checkoutRequest", request);
            return "orders/checkout";

        } catch (Exception ex) {
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            model.addAttribute("fieldErrors", null);
            model.addAttribute("checkoutRequest", request);
            return "orders/checkout";
        }
    }

    @GetMapping("/orders")
    public String myOrders(@RequestParam(defaultValue = "0") int page,
                           HttpSession session,
                           Model model) {
        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        var ordersPage = orderClient.getMyOrders(page, 10, "createdAt,desc");
        model.addAttribute("ordersPage", ordersPage);
        return "orders/my-orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id,
                               HttpSession session,
                               Model model) {
        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        model.addAttribute("order", orderClient.getMyOrderById(id));
        return "orders/order-details";
    }
}