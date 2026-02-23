package com.school.ppmg.computer_equipment_store_system_client.clients;

import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "computer-equipment-store-system-api-categories",url = "${backend.api.base-url}/api/categories")
public interface CategoryClient {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryResponse create(@Valid @RequestBody CategoryRequest request) ;
    @GetMapping("/{id}")
    CategoryResponse getById(@PathVariable Long id);

    @GetMapping
    PageResponse<CategoryResponse> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    );
    @GetMapping("/active")
    List<CategoryResponse> listActive();

    @PutMapping("/{id}")
    CategoryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    );

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id);

}
