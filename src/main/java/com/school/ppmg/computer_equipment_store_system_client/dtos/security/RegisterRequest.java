package com.school.ppmg.computer_equipment_store_system_client.dtos.security;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
}