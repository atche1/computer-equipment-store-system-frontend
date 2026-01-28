package com.school.ppmg.computer_equipment_store_system_client.dtos.category;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String slug,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}