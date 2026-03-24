package com.school.ppmg.computer_equipment_store_system_client.dtos.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean isActive;
}