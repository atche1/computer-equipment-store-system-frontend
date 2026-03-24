package com.school.ppmg.computer_equipment_store_system_client.dtos.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}