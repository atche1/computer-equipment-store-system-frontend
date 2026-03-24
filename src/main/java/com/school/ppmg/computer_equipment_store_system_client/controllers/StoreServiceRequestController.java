package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.StoreServiceClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.StoreServiceRequestClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service_request.CreateServiceRequestRequest;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.BackendException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class StoreServiceRequestController {

    private final StoreServiceRequestClient storeServiceRequestClient;
    private final StoreServiceClient storeServiceClient;

    @PostMapping("/services/{serviceId}/request")
    public String createServiceRequest(@PathVariable Long serviceId,
                                       @RequestParam String customerPhone,
                                       @RequestParam String description,
                                       HttpSession session,
                                       Model model) {
        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        request.setServiceId(serviceId);
        request.setCustomerPhone(customerPhone != null ? customerPhone.trim() : null);
        request.setDescription(description != null ? description.trim() : null);

        try {
            storeServiceRequestClient.create(request);
            return "redirect:/my-service-requests";
        } catch (BackendException ex) {
            model.addAttribute("service", storeServiceClient.getById(serviceId));
            model.addAttribute("requestForm", request);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("fieldErrors", ex.getFieldErrors());
            return "services/service-details";
        }
    }

    @GetMapping("/my-service-requests")
    public String myServiceRequests(@RequestParam(defaultValue = "0") int page,
                                    HttpSession session,
                                    Model model) {
        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        var requestsPage = storeServiceRequestClient.getMyRequests(page, 10, "createdAt,desc");
        model.addAttribute("requestsPage", requestsPage);
        return "service_requests/my-service-requests";
    }

    @GetMapping("/my-service-requests/{id}")
    public String myServiceRequestDetails(@PathVariable Long id,
                                          HttpSession session,
                                          Model model) {
        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        model.addAttribute("serviceRequest", storeServiceRequestClient.getMyRequestById(id));
        return "service_requests/service-request-details";
    }
}