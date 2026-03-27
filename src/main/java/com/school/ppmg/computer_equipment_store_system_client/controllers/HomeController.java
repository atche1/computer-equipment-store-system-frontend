package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.CategoryClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductImageClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.StoreServiceClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product.ProductResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product_image.ProductImageResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.service.ServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductClient productClient;
    private final CategoryClient categoryClient;
    private final ProductImageClient productImageClient;
    private final StoreServiceClient storeServiceClient;

    @GetMapping("/")
    public String home(Model model) {

        var latestProductsPage = productClient.getAll(
                null, null, true,
                null, null, null,
                null, null, null, null,
                0, 4, "createdAt,desc"
        );

        var categories = categoryClient.listActive();

        List<ProductResponse> latestProducts = latestProductsPage.getContent();

        Map<Long, String> latestProductImages = new LinkedHashMap<>();

        for (ProductResponse product : latestProducts) {
            List<ProductImageResponse> images = productImageClient.list(product.id());

            String imageUrl = images.stream()
                    .filter(img -> Boolean.TRUE.equals(img.isMain()))
                    .map(ProductImageResponse::imageUrl)
                    .filter(url -> url != null && !url.isBlank())
                    .findFirst()
                    .orElseGet(() -> images.stream()
                            .map(ProductImageResponse::imageUrl)
                            .filter(url -> url != null && !url.isBlank())
                            .findFirst()
                            .orElse("/images/placeholder.png"));

            latestProductImages.put(product.id(), imageUrl);
        }

        List<ServiceResponse> latestServices = storeServiceClient.getAllActive()
                .stream()
                .sorted(Comparator.comparing(ServiceResponse::createdAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(4)
                .toList();

        model.addAttribute("latestProducts", latestProducts);
        model.addAttribute("latestProductImages", latestProductImages);
        model.addAttribute("categories", categories);
        model.addAttribute("latestServices", latestServices);

        return "home";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}