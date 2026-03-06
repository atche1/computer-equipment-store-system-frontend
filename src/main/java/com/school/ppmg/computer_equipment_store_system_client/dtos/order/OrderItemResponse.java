package com.school.ppmg.computer_equipment_store_system_client.dtos.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productNameSnapshot,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal lineTotal
) {}