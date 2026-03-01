package com.school.ppmg.computer_equipment_store_system_client.dtos.product_image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {

    @NotBlank
    @Size(max = 1000)
    @Pattern(
            regexp = "^(https?://).+",
            message = "imageUrl must start with http:// or https://"
    )
    private String imageUrl;

    @NotNull
    private Boolean isMain;
}