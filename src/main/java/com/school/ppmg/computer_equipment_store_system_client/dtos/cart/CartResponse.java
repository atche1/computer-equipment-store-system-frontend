package com.school.ppmg.computer_equipment_store_system_client.dtos.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items,
        BigDecimal total
) {}