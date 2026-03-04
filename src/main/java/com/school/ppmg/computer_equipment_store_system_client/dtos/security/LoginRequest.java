package com.school.ppmg.computer_equipment_store_system_client.dtos.security;



import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}