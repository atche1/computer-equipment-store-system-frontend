package com.school.ppmg.computer_equipment_store_system_client.clients;

import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.order.CheckoutRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.order.OrderResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.order.UpdateOrderStatusRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "computer-equipment-store-system-api-orders",
        url = "${backend.api.base-url}/api/orders"
)
public interface OrderClient {

    @PostMapping("/checkout")
    OrderResponse checkout(@RequestBody CheckoutRequest request);

    @GetMapping("/my")
    PageResponse<OrderResponse> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    );

    @GetMapping("/my/{id}")
    OrderResponse getMyOrderById(@PathVariable Long id);

    @GetMapping
    PageResponse<OrderResponse> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    );

    @PutMapping("/{id}/status")
    OrderResponse updateStatus(@PathVariable Long id,
                               @RequestBody UpdateOrderStatusRequest request);
}