package com.school.ppmg.computer_equipment_store_system_client.config;

import com.school.ppmg.computer_equipment_store_system_client.clients.CartClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.CategoryClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.cart.CartResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryResponse;
import com.school.ppmg.computer_equipment_store_system_client.session.SessionCart;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private static final String SESSION_ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String SESSION_CART = "SESSION_CART";

    private final CategoryClient categoryClient;
    private final CartClient cartClient;

    @ModelAttribute("categories")
    public List<CategoryResponse> categories() {
        return categoryClient.listActive();
    }

    @ModelAttribute("cartItemCount")
    public int cartItemCount(HttpSession session) {
        boolean isLogged = session.getAttribute(SESSION_ACCESS_TOKEN) != null;

        if (isLogged) {
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

        SessionCart sessionCart = (SessionCart) session.getAttribute(SESSION_CART);
        if (sessionCart == null || sessionCart.getItems() == null) {
            return 0;
        }

        return sessionCart.getItems().values().stream()
                .mapToInt(qty -> qty == null ? 0 : qty)
                .sum();
    }
}