package com.school.ppmg.computer_equipment_store_system_client.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMyAccountRequest {

    @NotBlank(message = "First name is required.")
    @Size(max = 50, message = "First name must be at most 50 characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 50, message = "Last name must be at most 50 characters.")
    private String lastName;

    @NotBlank(message = "Phone number is required.")
    @Pattern(
            regexp = "^\\+?[0-9]{8,15}$",
            message = "Phone number must contain only digits and may start with +."
    )
    private String phone;
}