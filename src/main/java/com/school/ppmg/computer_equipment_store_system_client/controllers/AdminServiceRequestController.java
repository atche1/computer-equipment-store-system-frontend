package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.StoreServiceRequestClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service_request.UpdateServiceRequestStatusRequest;
import com.school.ppmg.computer_equipment_store_system_client.enums.ServiceRequestStatus;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/service-requests")
public class AdminServiceRequestController {

    private final StoreServiceRequestClient storeServiceRequestClient;

    @GetMapping
    public String allRequests(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) ServiceRequestStatus status,
                              HttpSession session,
                              Model model) {
        if (!"ADMIN".equals(session.getAttribute(AuthController.SESSION_ROLE))) {
            return "redirect:/";
        }

        var requestsPage = storeServiceRequestClient.getAll(status, page, 20, "createdAt,desc");
        model.addAttribute("requestsPage", requestsPage);
        model.addAttribute("statuses", ServiceRequestStatus.values());
        model.addAttribute("selectedStatus", status);
        return "admin/service-requests";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ServiceRequestStatus status,
                               HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute(AuthController.SESSION_ROLE))) {
            return "redirect:/";
        }

        storeServiceRequestClient.updateStatus(id, new UpdateServiceRequestStatusRequest(status));
        return "redirect:/admin/service-requests";
    }
}
