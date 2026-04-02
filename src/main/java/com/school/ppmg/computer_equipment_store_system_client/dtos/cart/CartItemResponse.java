package com.school.ppmg.computer_equipment_store_system_client.dtos.cart;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        Integer availableQuantity,
        String imageUrl
) {}