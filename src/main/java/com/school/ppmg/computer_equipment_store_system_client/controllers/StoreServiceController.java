package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.StoreServiceClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service.ServiceRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service.ServiceResponse;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.BackendException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class StoreServiceController {

    private final StoreServiceClient storeServiceClient;

    @GetMapping("/services")
    public String servicesPage(Model model) {
        model.addAttribute("services", storeServiceClient.getAllActive());
        return "services/list-services";
    }

    @GetMapping("/services/{id}")
    public String serviceDetails(@PathVariable Long id, Model model) {
        model.addAttribute("service", storeServiceClient.getById(id));
        model.addAttribute("requestForm", new com.school.ppmg.computer_equipment_store_system_client.dtos.service_request.CreateServiceRequestRequest());
        return "services/service-details";
    }

    @GetMapping("/admin/services")
    public String adminServices(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) Boolean isActive,
                                HttpSession session,
                                Model model) {
        if (!"ADMIN".equals(session.getAttribute(AuthController.SESSION_ROLE))) {
            return "redirect:/";
        }

        var servicesPage = storeServiceClient.getAllForAdmin(isActive, page, 20, "createdAt,desc");
        model.addAttribute("servicesPage", servicesPage);
        model.addAttribute("isActive", isActive);
        return "admin/services";
    }

    @GetMapping("/admin/services/create")
    public String createServicePage(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute(AuthController.SESSION_ROLE))) {
            return "redirect:/";
        }

        model.addAttribute("service", new ServiceRequest());
        return "services/create-service";
    }

    @PostMapping("/admin/services/create")
    public String createService(@ModelAttribute("service") ServiceRequest request,
                                HttpSession session,
                                Model model) {
        if (!"ADMIN".equals(session.getAttribute(AuthController.SESSION_ROLE))) {
            return "redirect:/";
        }

        try {
            storeServiceClient.create(request);
            return "redirect:/admin/services";
        } catch (BackendException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("service", request);
            return "services/create-service";
        }
    }

    @GetMapping("/admin/services/edit/{id}")
    public String editServicePage(@PathVariable Long id,
                                  HttpSession session,
                                  Model model) {
        if (!"ADMIN".equals(session.getAttribute(AuthController.SESSION_ROLE))) {
            return "redirect:/";
        }

        ServiceResponse service = storeServiceClient.getById(id);

        ServiceRequest request = new ServiceRequest();
        request.setName(service.name());
        request.setDescription(service.description());
        request.setPrice(service.price());
        request.setIsActive(service.isActive());

        model.addAttribute("service", request);
        model.addAttribute("serviceId", id);
        return "services/edit-service";
    }

    @PostMapping("/admin/services/edit/{id}")
    public String editService(@PathVariable Long id,
                              @ModelAttribute("service") ServiceRequest request,
                              HttpSession session,
                              Model model) {
        if (!"ADMIN".equals(session.getAttribute(AuthController.SESSION_ROLE))) {
            return "redirect:/";
        }

        try {
            storeServiceClient.update(id, request);
            return "redirect:/admin/services";
        } catch (BackendException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("serviceId", id);
            model.addAttribute("service", request);
            return "services/edit-service";
        }
    }

    @PostMapping("/admin/services/delete/{id}")
    public String deleteService(@PathVariable Long id, HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute(AuthController.SESSION_ROLE))) {
            return "redirect:/";
        }

        storeServiceClient.delete(id);
        return "redirect:/admin/services";
    }
}