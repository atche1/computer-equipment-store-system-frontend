package com.school.ppmg.computer_equipment_store_system_client.dtos.category;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String slug,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}