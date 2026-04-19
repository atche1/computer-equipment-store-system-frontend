package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.StoreServiceClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.StoreServiceRequestClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service_request.UpdateServiceRequestStatusRequest;
import com.school.ppmg.computer_equipment_store_system_client.enums.ServiceRequestStatus;
import com.school.ppmg.computer_equipment_store_system_client.security.AdminGuard;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/service-requests")
public class AdminServiceRequestController {

    private final StoreServiceRequestClient storeServiceRequestClient;
    private final StoreServiceClient storeServiceClient;
    private final AdminGuard adminGuard;

    @GetMapping
    public String allRequests(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "createdAt,desc") String sort,
                              @RequestParam(required = false) ServiceRequestStatus status,
                              @RequestParam(required = false) String q,
                              @RequestParam(required = false) Long serviceId,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                              HttpSession session,
                              Model model) {

        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        var requestsPage = storeServiceRequestClient.getAll(
                status,
                q,
                serviceId,
                dateFrom,
                dateTo,
                page,
                size,
                sort
        );

        model.addAttribute("requestsPage", requestsPage);
        model.addAttribute("statuses", ServiceRequestStatus.values());
        model.addAttribute("services", storeServiceClient.getAllActiveList());

        model.addAttribute("selectedStatus", status);
        model.addAttribute("q", q);
        model.addAttribute("serviceId", serviceId);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);

        return "admin/service-requests/service-requests";
    }

    @GetMapping("/{id}")
    public String requestDetails(@PathVariable Long id,
                                 HttpSession session,
                                 Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        model.addAttribute("serviceRequest", storeServiceRequestClient.getById(id));
        model.addAttribute("statuses", ServiceRequestStatus.values());

        return "admin/service-requests/service-request-details";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ServiceRequestStatus status,
                               @RequestParam(required = false) String q,
                               @RequestParam(required = false) Long serviceId,
                               @RequestParam(required = false) ServiceRequestStatus selectedStatus,
                               @RequestParam(required = false) String dateFrom,
                               @RequestParam(required = false) String dateTo,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "createdAt,desc") String sort,
                               HttpSession session) {

        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        storeServiceRequestClient.updateStatus(id, new UpdateServiceRequestStatusRequest(status));

        return "redirect:/admin/service-requests?page=" + page +
                "&size=" + size +
                "&sort=" + sort +
                (selectedStatus != null ? "&status=" + selectedStatus : "") +
                (q != null && !q.isBlank() ? "&q=" + q : "") +
                (serviceId != null ? "&serviceId=" + serviceId : "") +
                (dateFrom != null && !dateFrom.isBlank() ? "&dateFrom=" + dateFrom : "") +
                (dateTo != null && !dateTo.isBlank() ? "&dateTo=" + dateTo : "");
    }
}