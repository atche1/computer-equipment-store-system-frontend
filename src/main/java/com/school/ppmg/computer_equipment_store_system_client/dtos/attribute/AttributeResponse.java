package com.school.ppmg.computer_equipment_store_system_client.dtos.attribute;


import com.school.ppmg.computer_equipment_store_system_client.enums.AttributeDataType;

import java.time.LocalDateTime;

public record AttributeResponse(
        Long id,
        String name,
        AttributeDataType dataType,
        String unit,
        Boolean isFilterable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}