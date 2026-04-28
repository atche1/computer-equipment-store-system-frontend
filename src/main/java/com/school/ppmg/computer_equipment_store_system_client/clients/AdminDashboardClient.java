package com.school.ppmg.computer_equipment_store_system_client.clients;


import com.school.ppmg.computer_equipment_store_system_client.dtos.admin.AdminDashboardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "dashboard-client",
        url = "${backend.api.base-url}/api/admin/dashboard"
)
public interface AdminDashboardClient {

    @GetMapping
    AdminDashboardResponse getDashboard();
}
