package com.school.ppmg.computer_equipment_store_system_client.config;

import com.school.ppmg.computer_equipment_store_system_client.clients.CartClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.CategoryClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.cart.CartResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final CategoryClient categoryClient;
    private final CartClient cartClient;

    @ModelAttribute("categories")
    public List<CategoryResponse> categories() {
        return categoryClient.listActive();
    }
    @ModelAttribute("cartItemCount")
    public int cartItemCount() {
        try {
            CartResponse cart = cartClient.getMyCart();

            if (cart == null || cart.items() == null) {
                return 0;
            }

            return cart.items().stream()
                    .mapToInt(item -> item.quantity() == null ? 0 : item.quantity())
                    .sum();

        } catch (Exception e) {
            return 0;
        }
    }
}