package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.ProductClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductImageClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product_image.ProductImageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageClient productImageClient;
    private final ProductClient productClient;

    @GetMapping("/products/{productId}/images")
    public String page(@PathVariable Long productId, Model model) {
        var product = productClient.getById(productId);      // ✅ тук е промяната
        var images = productImageClient.list(productId);

        model.addAttribute("product", product);
        model.addAttribute("images", images);

        return "product_images/product-images";
    }

    @PostMapping("/products/{productId}/images")
    public String add(@PathVariable Long productId,
                      @RequestParam String imageUrl,
                      @RequestParam(required = false) Boolean isMain,
                      RedirectAttributes ra) {

        ProductImageRequest request = new ProductImageRequest();
        request.setImageUrl(imageUrl);
        request.setIsMain(Boolean.TRUE.equals(isMain));

        try {
            productImageClient.add(productId, request);
            ra.addFlashAttribute("success", "Image added.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/products/" + productId + "/images";
    }

    @PostMapping("/products/{productId}/images/{imageId}/main")
    public String setMain(@PathVariable Long productId, @PathVariable Long imageId) {
        productImageClient.setMain(productId, imageId);
        return "redirect:/products/" + productId + "/images";
    }

    @PostMapping("/products/{productId}/images/{imageId}/delete")
    public String delete(@PathVariable Long productId, @PathVariable Long imageId, RedirectAttributes ra) {
        productImageClient.delete(productId, imageId);
        ra.addFlashAttribute("success", "Image deleted.");
        return "redirect:/products/" + productId + "/images";
    }
}