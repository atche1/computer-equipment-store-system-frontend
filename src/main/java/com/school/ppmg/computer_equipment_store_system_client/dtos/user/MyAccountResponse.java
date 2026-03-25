package com.school.ppmg.computer_equipment_store_system_client.dtos.user;

import java.time.LocalDateTime;

public record MyAccountResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role,
        Boolean enabled,
        LocalDateTime createdAt
) {
}
