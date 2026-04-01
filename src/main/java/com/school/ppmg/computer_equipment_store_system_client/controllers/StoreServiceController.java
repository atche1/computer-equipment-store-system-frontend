package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.StoreServiceClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service.ServiceRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service.ServiceResponse;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.BackendException;
import com.school.ppmg.computer_equipment_store_system_client.security.AdminGuard;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StoreServiceController {

    private final StoreServiceClient storeServiceClient;
    private final AdminGuard adminGuard;

    @GetMapping("/services")
    public String servicesPage(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String sort,
            @RequestParam MultiValueMap<String, String> params,
            Model model
    ) {
        if (sort == null || sort.isBlank()) {
            sort = "createdAt,desc";
        }

        var result = storeServiceClient.getAllActive(q, minPrice, maxPrice, page, size, sort);

        model.addAttribute("services", result.getContent() != null ? result.getContent() : List.of());
        model.addAttribute("page", result);

        model.addAttribute("q", q);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);

        model.addAttribute("queryString", buildQueryString(params, sort, size));

        return "services/list-services";
    }

    @GetMapping("/services/{id}")
    public String serviceDetails(@PathVariable Long id, Model model) {
        model.addAttribute("service", storeServiceClient.getById(id));
        model.addAttribute("requestForm",
                new com.school.ppmg.computer_equipment_store_system_client.dtos.service_request.CreateServiceRequestRequest());
        return "services/service-details";
    }

    @GetMapping("/admin/services")
    public String adminServices(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) Boolean isActive,
                                HttpSession session,
                                Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        var servicesPage = storeServiceClient.getAllForAdmin(isActive, page, 20, "createdAt,desc");
        model.addAttribute("servicesPage", servicesPage);
        model.addAttribute("isActive", isActive);

        return "admin/services/services";
    }

    @GetMapping("/admin/services/create")
    public String createServicePage(HttpSession session, Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        model.addAttribute("service", new ServiceRequest());
        return "admin/services/create-service";
    }

    @PostMapping("/admin/services/create")
    public String createService(@ModelAttribute("service") ServiceRequest request,
                                HttpSession session,
                                Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        try {
            storeServiceClient.create(request);
            return "redirect:/admin/services";
        } catch (BackendException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("service", request);
            return "admin/services/create-service";
        }
    }

    @GetMapping("/admin/services/edit/{id}")
    public String editServicePage(@PathVariable Long id,
                                  HttpSession session,
                                  Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        ServiceResponse service = storeServiceClient.getById(id);

        ServiceRequest request = new ServiceRequest();
        request.setName(service.name());
        request.setDescription(service.description());
        request.setPrice(service.price());
        request.setIsActive(service.isActive());

        model.addAttribute("service", request);
        model.addAttribute("serviceId", id);

        return "admin/services/edit-service";
    }

    @PostMapping("/admin/services/edit/{id}")
    public String editService(@PathVariable Long id,
                              @ModelAttribute("service") ServiceRequest request,
                              HttpSession session,
                              Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        try {
            storeServiceClient.update(id, request);
            return "redirect:/admin/services";
        } catch (BackendException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("serviceId", id);
            model.addAttribute("service", request);
            return "admin/services/edit-service";
        }
    }

    @PostMapping("/admin/services/delete/{id}")
    public String deleteService(@PathVariable Long id, HttpSession session) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        storeServiceClient.delete(id);
        return "redirect:/admin/services";
    }
    private String buildQueryString(MultiValueMap<String, String> params,
                                    String sort,
                                    int size) {
        StringBuilder sb = new StringBuilder();

        params.forEach((key, values) -> {
            if ("page".equals(key)) return;
            if (values == null) return;

            for (String value : values) {
                if (value != null && !value.isBlank()) {
                    sb.append("&")
                            .append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                            .append("=")
                            .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
            }
        });

        if (!params.containsKey("sort") && sort != null && !sort.isBlank()) {
            sb.append("&sort=").append(URLEncoder.encode(sort, StandardCharsets.UTF_8));
        }

        if (!params.containsKey("size")) {
            sb.append("&size=").append(size);
        }

        return sb.toString();
    }
}