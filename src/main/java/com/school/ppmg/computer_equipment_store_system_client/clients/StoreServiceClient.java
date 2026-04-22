package com.school.ppmg.computer_equipment_store_system_client.clients;

import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service.ServiceRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service.ServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(
        name = "computer-equipment-store-system-api-services",
        url = "${backend.api.base-url}/api/services"
)
public interface StoreServiceClient {

    @PostMapping
    ServiceResponse create(@RequestBody ServiceRequest request);

    @GetMapping("/{id}")
    ServiceResponse getById(@PathVariable Long id);

    @GetMapping("/active-list")
    List<ServiceResponse> getAllActiveList();

    @GetMapping("/active")
    PageResponse<ServiceResponse> getAllActive(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String sort
    );

    @GetMapping
    PageResponse<ServiceResponse> getAllForAdmin(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    );

    @PutMapping("/{id}")
    ServiceResponse update(@PathVariable Long id, @RequestBody ServiceRequest request);

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id);
}