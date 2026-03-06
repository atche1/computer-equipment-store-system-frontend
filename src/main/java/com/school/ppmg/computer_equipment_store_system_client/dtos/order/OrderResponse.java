package com.school.ppmg.computer_equipment_store_system_client.dtos.order;

import com.school.ppmg.computer_equipment_store_system_client.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        BigDecimal totalAmount,
        String deliveryName,
        String deliveryPhone,
        String deliveryAddress,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {}