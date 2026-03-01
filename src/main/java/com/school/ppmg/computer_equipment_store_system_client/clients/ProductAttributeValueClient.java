package com.school.ppmg.computer_equipment_store_system_client.clients;

import com.school.ppmg.computer_equipment_store_system_client.dtos.product_attribute_value.ProductAttributeValueRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product_attribute_value.ProductAttributeValueResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "computer-equipment-store-system-api-product-attribute-values",
        url = "${backend.api.base-url}/api/products"
)
public interface ProductAttributeValueClient {

    @GetMapping("/{productId}/attribute-values")
    List<ProductAttributeValueResponse> list(@PathVariable Long productId);

    @PutMapping("/{productId}/attribute-values")
    List<ProductAttributeValueResponse> upsertBatch(
            @PathVariable Long productId,
            @RequestBody List<ProductAttributeValueRequest> requests
    );
}