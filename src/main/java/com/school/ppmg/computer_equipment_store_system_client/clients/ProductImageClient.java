package com.school.ppmg.computer_equipment_store_system_client.clients;

import com.school.ppmg.computer_equipment_store_system_client.dtos.product_image.ProductImageRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product_image.ProductImageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@FeignClient(
        name = "computer-equipment-store-system-api-product-images",
        url = "${backend.api.base-url}/api/products",
        configuration = com.school.ppmg.computer_equipment_store_system_client.config.FeignMultipartConfig.class
)
public interface ProductImageClient {

    @PostMapping(value="/{productId}/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ProductImageResponse upload(@PathVariable Long productId,
                                @RequestPart("file") MultipartFile file,
                                @RequestParam(value = "isMain", required = false) Boolean isMain);


    @GetMapping("/{productId}/images")
    List<ProductImageResponse> list(@PathVariable Long productId);

    @PostMapping("/{productId}/images")
    ProductImageResponse add(@PathVariable Long productId, @RequestBody ProductImageRequest request);

    @DeleteMapping("/{productId}/images/{imageId}")
    void delete(@PathVariable Long productId, @PathVariable Long imageId);

    @PutMapping("/{productId}/images/{imageId}/main")
    void setMain(@PathVariable Long productId, @PathVariable Long imageId);
}