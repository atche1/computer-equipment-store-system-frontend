package com.school.ppmg.computer_equipment_store_system_client.clients;

import com.school.ppmg.computer_equipment_store_system_client.dtos.user.ChangePasswordRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.user.MyAccountResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.user.UpdateMyAccountRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "computer-equipment-store-system-api-users",
        url = "${backend.api.base-url}/api/users"
)
public interface UserClient {

    @GetMapping("/me")
    MyAccountResponse getMyAccount();
    @PutMapping("/me")
    MyAccountResponse updateMyAccount(@RequestBody UpdateMyAccountRequest request);
    @PutMapping("/change-password")
    void changePassword(@RequestBody ChangePasswordRequest request);
}
