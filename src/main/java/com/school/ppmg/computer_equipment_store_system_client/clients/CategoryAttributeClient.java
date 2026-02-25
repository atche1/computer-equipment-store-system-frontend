package com.school.ppmg.computer_equipment_store_system_client.clients;

import com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category_attribute.CategoryAttributeAddRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@FeignClient(name = "computer-equipment-store-system-api-category-attributes",url = "${backend.api.base-url}/api/categories")
public interface CategoryAttributeClient {

    @GetMapping("/{categoryId}/attributes")
    List<AttributeResponse> list(@PathVariable Long categoryId);

    @PostMapping("/{categoryId}/attributes")
    @ResponseStatus(HttpStatus.CREATED)
    AttributeResponse add(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryAttributeAddRequest request
    );

    @DeleteMapping("/{categoryId}/attributes/{attributeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void remove(
            @PathVariable Long categoryId,
            @PathVariable Long attributeId
    );
}