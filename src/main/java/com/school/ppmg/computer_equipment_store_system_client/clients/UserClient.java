package com.school.ppmg.computer_equipment_store_system_client.clients;

import com.school.ppmg.computer_equipment_store_system_client.dtos.user.MyAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "computer-equipment-store-system-api-users",
        url = "${backend.api.base-url}/api/users"
)
public interface UserClient {

    @GetMapping("/me")
    MyAccountResponse getMyAccount();
}
