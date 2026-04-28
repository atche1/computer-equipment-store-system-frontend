package com.school.ppmg.computer_equipment_store_system_client.dtos.admin;

import java.util.Map;

public record AdminDashboardResponse(
        long totalOrders,
        Map<String, Long> ordersByStatus,
        long totalServiceRequests,
        Map<String, Long> serviceRequestsByStatus,
        long totalUsers,
        long totalProducts,
        long totalServices
) {
}