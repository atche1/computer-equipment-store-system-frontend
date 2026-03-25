package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.CartClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductImageClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.cart.MergeCartRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.cart.UpdateCartItemRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product.ProductResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product_image.ProductImageResponse;
import com.school.ppmg.computer_equipment_store_system_client.session.SessionCart;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    public static final String SESSION_CART = "SESSION_CART";
    public static final String SESSION_ACCESS_TOKEN = "ACCESS_TOKEN";

    private final CartClient cartClient;
    private final ProductClient productClient;
    private final ProductImageClient productImageClient;

    private boolean isLogged(HttpSession session) {
        Object token = session.getAttribute(SESSION_ACCESS_TOKEN);
        return token != null && !token.toString().isBlank();
    }

    private SessionCart getOrCreateSessionCart(HttpSession session) {
        SessionCart cart = (SessionCart) session.getAttribute(SESSION_CART);
        if (cart == null) {
            cart = new SessionCart();
            session.setAttribute(SESSION_CART, cart);
        }
        return cart;
    }

    // ---------- VIEW ----------
    @GetMapping
    public String view(HttpSession session, Model model) {

        if (isLogged(session)) {
            var dbCart = cartClient.getMyCart();
            model.addAttribute("dbCart", dbCart);
            return "cart/cart";
        }

        SessionCart sc = getOrCreateSessionCart(session);

        List<GuestCartLine> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> e : sc.getItems().entrySet()) {
            Long productId = e.getKey();
            Integer qty = e.getValue();

            ProductResponse p = productClient.getById(productId);
            BigDecimal lineTotal = p.price().multiply(BigDecimal.valueOf(qty));
            String imageUrl = resolveProductImage(productId);

            lines.add(new GuestCartLine(p, qty, lineTotal, imageUrl));
            total = total.add(lineTotal);
        }

        model.addAttribute("guestLines", lines);
        model.addAttribute("guestTotal", total);

        return "cart/cart";
    }

    public record GuestCartLine(
            ProductResponse product,
            Integer qty,
            BigDecimal lineTotal,
            String imageUrl
    ) {}

    // ---------- ADD ----------
    @PostMapping("/add")
    public String add(@RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int qty,
                      HttpSession session) {

        if (!isLogged(session)) {
            SessionCart sc = getOrCreateSessionCart(session);
            sc.add(productId, qty);
            return "redirect:/cart";
        }

        // logged -> add to DB cart via merge (adds qty)
        MergeCartRequest req = new MergeCartRequest();
        req.setItems(Map.of(productId, qty));
        cartClient.mergeCart(req);

        return "redirect:/cart";
    }

    // ---------- UPDATE (GUEST) ----------
    @PostMapping("/update")
    public String updateGuest(@RequestParam Long productId,
                              @RequestParam int qty,
                              HttpSession session) {

        if (isLogged(session)) {
            // за логнат ще направим update през API (виж т.3 по-долу)
            return "redirect:/cart";
        }

        SessionCart sc = getOrCreateSessionCart(session);
        sc.setQty(productId, qty);
        return "redirect:/cart";
    }

    // ---------- REMOVE (GUEST) ----------
    @PostMapping("/remove")
    public String removeGuest(@RequestParam Long productId,
                              HttpSession session) {

        if (isLogged(session)) {
            // за логнат ще направим remove през API (виж т.3 по-долу)
            return "redirect:/cart";
        }

        SessionCart sc = getOrCreateSessionCart(session);
        sc.remove(productId);
        return "redirect:/cart";
    }
    @PostMapping("/update-db")
    public String updateDb(@RequestParam Long itemId,
                           @RequestParam int qty,
                           HttpSession session) {
        if (session.getAttribute(SESSION_ACCESS_TOKEN) == null) return "redirect:/cart";

        cartClient.updateItem(itemId, new UpdateCartItemRequest(qty));
        return "redirect:/cart";
    }

    @PostMapping("/remove-db")
    public String removeDb(@RequestParam Long itemId,
                           HttpSession session) {
        if (session.getAttribute(SESSION_ACCESS_TOKEN) == null) return "redirect:/cart";

        cartClient.deleteItem(itemId);
        return "redirect:/cart";
    }
    private String resolveProductImage(Long productId) {
        List<ProductImageResponse> images = productImageClient.list(productId);

        ProductImageResponse mainImage = images.stream()
                .filter(img -> Boolean.TRUE.equals(img.isMain()))
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));

        return mainImage != null ? mainImage.imageUrl() : null;
    }
}