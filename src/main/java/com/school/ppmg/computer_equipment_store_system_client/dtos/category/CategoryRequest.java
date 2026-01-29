package com.school.ppmg.computer_equipment_store_system_client.dtos.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 150)
    private String slug;

    private Boolean isActive = true;
}