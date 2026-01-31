package com.school.ppmg.computer_equipment_store_system_client.dtos.attribute;

import com.school.ppmg.computer_equipment_store_system_client.enums.AttributeDataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeRequest {
    @NotBlank @Size(max = 120)
    private String name;
    @NotNull
    private AttributeDataType dataType;
    @Size(max = 30)
    private String unit;
    private Boolean isFilterable = true;
}
