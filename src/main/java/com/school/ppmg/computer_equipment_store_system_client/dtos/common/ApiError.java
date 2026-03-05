package com.school.ppmg.computer_equipment_store_system_client.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private String message;
    private Map<String, String> fieldErrors;


}