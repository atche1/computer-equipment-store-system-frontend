package com.school.ppmg.computer_equipment_store_system_client.dtos.service_request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceRequestRequest {
    private Long serviceId;
    private String customerPhone;
    private String description;
}