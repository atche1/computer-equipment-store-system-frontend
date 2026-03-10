package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.ProductClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductImageClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product_image.ProductImageRequest;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.BackendException;
import com.school.ppmg.computer_equipment_store_system_client.security.AdminGuard;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageClient productImageClient;
    private final ProductClient productClient;
    private final AdminGuard adminGuard;

    @Value("${backend.api.base-url}")
    private String backendBaseUrl;

    @PostMapping("/products/{productId}/images/upload")
    public String upload(@PathVariable Long productId,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam(required = false) Boolean isMain,
                         HttpSession session,
                         RedirectAttributes ra) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        try {
            productImageClient.upload(productId, file, isMain);
            ra.addFlashAttribute("success", "Image uploaded.");
        } catch (BackendException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/products/" + productId + "/images";
    }

    @GetMapping("/products/{productId}/images")
    public String page(@PathVariable Long productId,
                       HttpSession session,
                       Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        var product = productClient.getById(productId);
        var images = productImageClient.list(productId);

        model.addAttribute("product", product);
        model.addAttribute("images", images);
        model.addAttribute("backendBaseUrl", backendBaseUrl);

        return "admin/product_images/product-images";
    }

    @PostMapping("/products/{productId}/images")
    public String add(@PathVariable Long productId,
                      @RequestParam String imageUrl,
                      @RequestParam(required = false) Boolean isMain,
                      HttpSession session,
                      RedirectAttributes ra) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        ProductImageRequest request = new ProductImageRequest();
        request.setImageUrl(imageUrl);
        request.setIsMain(Boolean.TRUE.equals(isMain));

        try {
            productImageClient.add(productId, request);
            ra.addFlashAttribute("success", "Image added.");
        } catch (BackendException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/products/" + productId + "/images";
    }

    @PostMapping("/products/{productId}/images/{imageId}/main")
    public String setMain(@PathVariable Long productId,
                          @PathVariable Long imageId,
                          HttpSession session) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        productImageClient.setMain(productId, imageId);
        return "redirect:/products/" + productId + "/images";
    }

    @PostMapping("/products/{productId}/images/{imageId}/delete")
    public String delete(@PathVariable Long productId,
                         @PathVariable Long imageId,
                         HttpSession session,
                         RedirectAttributes ra) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        productImageClient.delete(productId, imageId);
        ra.addFlashAttribute("success", "Image deleted.");
        return "redirect:/products/" + productId + "/images";
    }
}