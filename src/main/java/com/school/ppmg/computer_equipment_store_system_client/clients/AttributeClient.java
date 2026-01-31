package com.school.ppmg.computer_equipment_store_system_client.clients;

import com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import com.school.ppmg.computer_equipment_store_system_client.enums.AttributeDataType;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@FeignClient(name = "computer-equipment-store-system-api-attributes",url = "${backend.api.base-url}/api/attributes")
public interface AttributeClient {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttributeResponse create(@Valid @RequestBody AttributeRequest request);

    @GetMapping("/{id}")
    public AttributeResponse getById(@PathVariable Long id);

    @GetMapping
    PageResponse<AttributeResponse> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) AttributeDataType dataType,
            @RequestParam(required = false) Boolean filterable,
            @RequestParam(required = false) String unit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) ;

    @PutMapping("/{id}")
    public AttributeResponse update(@PathVariable Long id, @Valid @RequestBody AttributeRequest request);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id);

}
