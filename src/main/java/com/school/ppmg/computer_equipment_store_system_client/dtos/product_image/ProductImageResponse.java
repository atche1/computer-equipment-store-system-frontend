package com.school.ppmg.computer_equipment_store_system_client.dtos.product_image;

public record ProductImageResponse(
        Long id,
        String imageUrl,
        Boolean isMain
) {}