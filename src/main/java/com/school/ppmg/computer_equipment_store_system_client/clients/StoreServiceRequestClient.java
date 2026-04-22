package com.school.ppmg.computer_equipment_store_system_client.clients;

import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service_request.CreateServiceRequestRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service_request.ServiceRequestResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service_request.UpdateServiceRequestStatusRequest;
import com.school.ppmg.computer_equipment_store_system_client.enums.ServiceRequestStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@FeignClient(
        name = "computer-equipment-store-system-api-service-requests",
        url = "${backend.api.base-url}/api/service-requests"
)
public interface StoreServiceRequestClient {

    @PostMapping
    ServiceRequestResponse create(@RequestBody CreateServiceRequestRequest request);

    @GetMapping("/my")
    PageResponse<ServiceRequestResponse> getMyRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    );

    @GetMapping("/my/{id}")
    ServiceRequestResponse getMyRequestById(@PathVariable Long id);

    @GetMapping
    PageResponse<ServiceRequestResponse> getAll(
            @RequestParam(required = false) ServiceRequestStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    );

    @GetMapping("/{id}")
    ServiceRequestResponse getById(@PathVariable Long id);

    @PutMapping("/{id}/status")
    ServiceRequestResponse updateStatus(@PathVariable Long id,
                                        @RequestBody UpdateServiceRequestStatusRequest request);
}