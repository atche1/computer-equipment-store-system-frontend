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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public String adminServices(@RequestParam(required = false) String q,
                                @RequestParam(required = false) Boolean isActive,
                                @RequestParam(required = false) BigDecimal minPrice,
                                @RequestParam(required = false) BigDecimal maxPrice,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String sort,
                                @RequestParam MultiValueMap<String, String> params,
                                HttpSession session,
                                Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        if (sort == null || sort.isBlank()) {
            sort = "createdAt,desc";
        }

        var servicesPage = storeServiceClient.getAllForAdmin(
                q,
                isActive,
                minPrice,
                maxPrice,
                page,
                size,
                sort
        );

        model.addAttribute("servicesPage", servicesPage);
        model.addAttribute("services", servicesPage.getContent() != null ? servicesPage.getContent() : List.of());

        model.addAttribute("q", q);
        model.addAttribute("isActive", isActive);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);

        model.addAttribute("queryString", buildQueryString(params, sort, size));

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

    private String buildQueryString(MultiValueMap<String, String> params, String sort, int size) {
        Map<String, String> flat = new LinkedHashMap<>();

        for (var e : params.entrySet()) {
            String key = e.getKey();
            if ("page".equals(key)) continue;
            if (e.getValue() == null || e.getValue().isEmpty()) continue;

            String value = e.getValue().get(0);
            if (value == null || value.isBlank()) continue;

            flat.put(key, value);
        }

        if (!flat.containsKey("sort") && sort != null && !sort.isBlank()) {
            flat.put("sort", sort);
        }

        if (!flat.containsKey("size")) {
            flat.put("size", String.valueOf(size));
        }

        StringBuilder sb = new StringBuilder();
        for (var e : flat.entrySet()) {
            sb.append("&")
                    .append(urlEnc(e.getKey()))
                    .append("=")
                    .append(urlEnc(e.getValue()));
        }

        return sb.toString();
    }

    private String urlEnc(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return s;
        }
    }
}