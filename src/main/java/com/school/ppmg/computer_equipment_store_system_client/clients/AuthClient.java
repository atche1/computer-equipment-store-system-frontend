package com.school.ppmg.computer_equipment_store_system_client.clients;


import com.school.ppmg.computer_equipment_store_system_client.dtos.security.AuthResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.security.LoginRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.security.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="computer-equipment-store-system-api-auth", url="${backend.api.base-url}/api/auth")
public interface AuthClient {

    @PostMapping("/register")
    void register(@RequestBody RegisterRequest req);

    @PostMapping("/login")
    AuthResponse login(@RequestBody LoginRequest req);
}