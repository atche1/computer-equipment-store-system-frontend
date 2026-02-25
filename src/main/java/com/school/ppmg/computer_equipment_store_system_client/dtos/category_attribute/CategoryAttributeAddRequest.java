package com.school.ppmg.computer_equipment_store_system_client.dtos.category_attribute;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAttributeAddRequest {
    @NotNull
    private Long attributeId;
}
