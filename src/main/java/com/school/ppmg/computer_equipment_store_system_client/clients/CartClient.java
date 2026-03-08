package com.school.ppmg.computer_equipment_store_system_client.clients;

import com.school.ppmg.computer_equipment_store_system_client.dtos.cart.CartResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.cart.MergeCartRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.cart.UpdateCartItemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "computer-equipment-store-system-api-cart",url = "${backend.api.base-url}/api/cart")
public interface CartClient {

    @GetMapping
    CartResponse getMyCart();

    @PostMapping("/merge")
    CartResponse mergeCart(@RequestBody MergeCartRequest request);
    @PutMapping("/items/{itemId}")
    CartResponse updateItem(@PathVariable Long itemId,
                            @RequestBody UpdateCartItemRequest request);

    @DeleteMapping("/items/{itemId}")
    CartResponse deleteItem(@PathVariable Long itemId);
}