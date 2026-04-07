package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.CategoryClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductAttributeValueClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductImageClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product.ProductRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product.ProductResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product_attribute_value.ProductAttributeValueResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product_image.ProductImageResponse;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.ApiConflictException;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.BackendException;
import com.school.ppmg.computer_equipment_store_system_client.security.AdminGuard;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductClient productClient;
    private final CategoryClient categoryClient;
    private final com.school.ppmg.computer_equipment_store_system_client.clients.CategoryAttributeClient categoryAttributeClient;
    private final ProductImageClient productImageClient;
    private final ProductAttributeValueClient productAttributeValueClient;
    private final AdminGuard adminGuard;

    // =========================================================
    // PUBLIC CATALOG
    // =========================================================

    @GetMapping("/products")
    public String productsPage(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String sort,
            @RequestParam org.springframework.util.MultiValueMap<String, String> params,
            Model model
    ) {
        if (sort == null || sort.isBlank()) {
            sort = "createdAt,desc";
        }

        List<com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeResponse> filterableAttributes = List.of();
        if (categoryId != null) {
            filterableAttributes = categoryAttributeClient.list(categoryId).stream()
                    .filter(a -> Boolean.TRUE.equals(a.isFilterable()))
                    .toList();
        }

        List<String> attrText = new ArrayList<>();
        List<String> attrNumMin = new ArrayList<>();
        List<String> attrNumMax = new ArrayList<>();
        List<String> attrBool = new ArrayList<>();

        Map<Long, String> uiText = new HashMap<>();
        Map<Long, String> uiNumMin = new HashMap<>();
        Map<Long, String> uiNumMax = new HashMap<>();
        Map<Long, String> uiBool = new HashMap<>();

        for (var entry : params.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() == null || entry.getValue().isEmpty()) continue;

            String val = entry.getValue().get(0);
            if (val == null || val.isBlank()) continue;

            if (key.startsWith("attrText_")) {
                Long id = tryParseIdSuffix(key, "attrText_");
                if (id != null) {
                    attrText.add(id + ":" + val.trim());
                    uiText.put(id, val.trim());
                }
            } else if (key.startsWith("attrNumMin_")) {
                Long id = tryParseIdSuffix(key, "attrNumMin_");
                if (id != null) {
                    attrNumMin.add(id + ":" + val.trim());
                    uiNumMin.put(id, val.trim());
                }
            } else if (key.startsWith("attrNumMax_")) {
                Long id = tryParseIdSuffix(key, "attrNumMax_");
                if (id != null) {
                    attrNumMax.add(id + ":" + val.trim());
                    uiNumMax.put(id, val.trim());
                }
            } else if (key.startsWith("attrBool_")) {
                Long id = tryParseIdSuffix(key, "attrBool_");
                if (id != null) {
                    attrBool.add(id + ":" + val.trim());
                    uiBool.put(id, val.trim());
                }
            }
        }

        PageResponse<ProductResponse> result = productClient.getAll(
                q,
                categoryId,
                true,
                minPrice,
                maxPrice,
                inStock,
                attrText.isEmpty() ? null : attrText,
                attrNumMin.isEmpty() ? null : attrNumMin,
                attrNumMax.isEmpty() ? null : attrNumMax,
                attrBool.isEmpty() ? null : attrBool,
                page,
                size,
                sort
        );

        List<CategoryResponse> categories = categoryClient.listActive();
        List<ProductResponse> products = result.getContent() != null ? result.getContent() : List.of();
        Map<Long, String> productImages = buildProductImageMap(products);

        model.addAttribute("page", result);
        model.addAttribute("products", products);
        model.addAttribute("productImages", productImages);
        model.addAttribute("categories", categories);

        model.addAttribute("q", q);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("isActive", isActive);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStock", inStock);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        model.addAttribute("filterableAttributes", filterableAttributes);
        model.addAttribute("uiText", uiText);
        model.addAttribute("uiNumMin", uiNumMin);
        model.addAttribute("uiNumMax", uiNumMax);
        model.addAttribute("uiBool", uiBool);

        model.addAttribute("queryString", buildQueryString(params, sort, size));

        return "products/list-products";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        ProductResponse product = productClient.getById(id);

        List<ProductImageResponse> images = productImageClient.list(id);
        List<ProductAttributeValueResponse> attributes = productAttributeValueClient.list(id);

        ProductImageResponse mainImage = images.stream()
                .filter(img -> Boolean.TRUE.equals(img.isMain()))
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));

        String brandName = extractBrandName(product.name());

        PageResponse<ProductResponse> allProductsPage = productClient.getAll(
                null,
                null,
                true,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                200,
                "createdAt,desc"
        );

        List<ProductResponse> allProducts = allProductsPage.getContent() != null
                ? allProductsPage.getContent()
                : List.of();

        List<ProductResponse> sameCategoryProducts = allProducts.stream()
                .filter(p -> p.id() != null && !p.id().equals(product.id()))
                .filter(p -> Boolean.TRUE.equals(p.isActive()))
                .filter(p -> product.categoryId() != null && product.categoryId().equals(p.categoryId()))
                .limit(8)
                .toList();

        List<ProductResponse> sameBrandProducts = allProducts.stream()
                .filter(p -> p.id() != null && !p.id().equals(product.id()))
                .filter(p -> Boolean.TRUE.equals(p.isActive()))
                .filter(p -> {
                    String currentBrand = extractBrandName(p.name());
                    return !brandName.isBlank() && brandName.equalsIgnoreCase(currentBrand);
                })
                .limit(8)
                .toList();

        Map<Long, String> sameCategoryProductImages = buildProductImageMap(sameCategoryProducts);
        Map<Long, String> sameBrandProductImages = buildProductImageMap(sameBrandProducts);

        model.addAttribute("product", product);
        model.addAttribute("images", images);
        model.addAttribute("mainImage", mainImage);
        model.addAttribute("attributes", attributes);
        model.addAttribute("brandName", brandName);

        model.addAttribute("sameCategoryProducts", sameCategoryProducts);
        model.addAttribute("sameCategoryProductImages", sameCategoryProductImages);

        model.addAttribute("sameBrandProducts", sameBrandProducts);
        model.addAttribute("sameBrandProductImages", sameBrandProductImages);

        return "products/product-details";
    }

    // =========================================================
    // ADMIN PRODUCT MANAGEMENT
    // =========================================================

    @GetMapping("/admin/products")
    public String adminProductsPage(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam org.springframework.util.MultiValueMap<String, String> params,
            HttpSession session,
            Model model
    ) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        if (sort == null || sort.isBlank()) {
            sort = "createdAt,desc";
        }

        List<com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeResponse> filterableAttributes = List.of();
        if (categoryId != null) {
            filterableAttributes = categoryAttributeClient.list(categoryId).stream()
                    .filter(a -> Boolean.TRUE.equals(a.isFilterable()))
                    .toList();
        }

        List<String> attrText = new ArrayList<>();
        List<String> attrNumMin = new ArrayList<>();
        List<String> attrNumMax = new ArrayList<>();
        List<String> attrBool = new ArrayList<>();

        Map<Long, String> uiText = new HashMap<>();
        Map<Long, String> uiNumMin = new HashMap<>();
        Map<Long, String> uiNumMax = new HashMap<>();
        Map<Long, String> uiBool = new HashMap<>();

        for (var entry : params.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() == null || entry.getValue().isEmpty()) continue;

            String val = entry.getValue().get(0);
            if (val == null || val.isBlank()) continue;

            if (key.startsWith("attrText_")) {
                Long id = tryParseIdSuffix(key, "attrText_");
                if (id != null) {
                    attrText.add(id + ":" + val.trim());
                    uiText.put(id, val.trim());
                }
            } else if (key.startsWith("attrNumMin_")) {
                Long id = tryParseIdSuffix(key, "attrNumMin_");
                if (id != null) {
                    attrNumMin.add(id + ":" + val.trim());
                    uiNumMin.put(id, val.trim());
                }
            } else if (key.startsWith("attrNumMax_")) {
                Long id = tryParseIdSuffix(key, "attrNumMax_");
                if (id != null) {
                    attrNumMax.add(id + ":" + val.trim());
                    uiNumMax.put(id, val.trim());
                }
            } else if (key.startsWith("attrBool_")) {
                Long id = tryParseIdSuffix(key, "attrBool_");
                if (id != null) {
                    attrBool.add(id + ":" + val.trim());
                    uiBool.put(id, val.trim());
                }
            }
        }

        PageResponse<ProductResponse> result = productClient.getAll(
                q,
                categoryId,
                isActive,
                minPrice,
                maxPrice,
                inStock,
                attrText.isEmpty() ? null : attrText,
                attrNumMin.isEmpty() ? null : attrNumMin,
                attrNumMax.isEmpty() ? null : attrNumMax,
                attrBool.isEmpty() ? null : attrBool,
                page,
                size,
                sort
        );

        model.addAttribute("page", result);
        model.addAttribute("products", result.getContent());
        model.addAttribute("categories", categoryClient.listActive());

        model.addAttribute("q", q);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("isActive", isActive);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStock", inStock);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        model.addAttribute("filterableAttributes", filterableAttributes);
        model.addAttribute("uiText", uiText);
        model.addAttribute("uiNumMin", uiNumMin);
        model.addAttribute("uiNumMax", uiNumMax);
        model.addAttribute("uiBool", uiBool);

        model.addAttribute("queryString", buildQueryString(params, sort, size));

        return "admin/products/list-products-admin";
    }

    @GetMapping("/admin/products/create")
    public String createProduct(HttpSession session, Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        model.addAttribute("product", new ProductRequest());
        model.addAttribute("categories", categoryClient.listActive());
        return "admin/products/create-product";
    }

    @PostMapping("/admin/products/create")
    public String submitProduct(@Valid @ModelAttribute("product") ProductRequest request,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryClient.listActive());
            return "admin/products/create-product";
        }

        try {
            productClient.create(request);
            return "redirect:/admin/products";
        } catch (BackendException ex) {
            bindingResult.addError(new ObjectError("product", ex.getMessage()));
            model.addAttribute("categories", categoryClient.listActive());
            return "admin/products/create-product";
        }
    }

    @GetMapping("/admin/products/{id}/edit")
    public String editProduct(@PathVariable Long id, HttpSession session, Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        ProductResponse p = productClient.getById(id);

        ProductRequest req = new ProductRequest();
        req.setName(p.name());
        req.setDescription(p.description());
        req.setPrice(p.price());
        req.setQuantity(p.quantity());
        req.setIsActive(p.isActive());
        req.setCategoryId(p.categoryId());

        model.addAttribute("product", req);
        model.addAttribute("productId", id);
        model.addAttribute("categories", categoryClient.listActive());

        return "admin/products/edit-product";
    }

    @PostMapping("/admin/products/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("product") ProductRequest request,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryClient.listActive());
            return "admin/products/edit-product";
        }

        try {
            productClient.update(id, request);
            return "redirect:/admin/products";
        } catch (BackendException ex) {
            bindingResult.addError(new ObjectError("product", ex.getMessage()));
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryClient.listActive());
            return "admin/products/edit-product";
        }
    }

    @PostMapping("/admin/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        productClient.delete(id);
        return "redirect:/admin/products";
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Long tryParseIdSuffix(String key, String prefix) {
        try {
            return Long.parseLong(key.substring(prefix.length()));
        } catch (Exception ex) {
            return null;
        }
    }

    private String buildQueryString(org.springframework.util.MultiValueMap<String, String> params, String sort, int size) {
        Map<String, String> flat = new java.util.LinkedHashMap<>();

        for (var e : params.entrySet()) {
            String k = e.getKey();
            if ("page".equals(k)) continue;
            if (e.getValue() == null || e.getValue().isEmpty()) continue;

            String v = e.getValue().get(0);
            if (v == null || v.isBlank()) continue;

            flat.put(k, v);
        }

        if (!flat.containsKey("sort")) {
            flat.put("sort", sort);
        }
        if (!flat.containsKey("size")) {
            flat.put("size", String.valueOf(size));
        }

        StringBuilder sb = new StringBuilder();
        for (var e : flat.entrySet()) {
            sb.append("&")
                    .append(urlEnc(e.getKey()))
                    .append("=")
                    .append(urlEnc(e.getValue()));
        }
        return sb.toString();
    }

    private String urlEnc(String s) {
        try {
            return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return s;
        }
    }
    private String extractBrandName(String productName) {
        if (productName == null || productName.isBlank()) {
            return "";
        }

        String[] parts = productName.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }

    private Map<Long, String> buildProductImageMap(List<ProductResponse> products) {
        Map<Long, String> imageMap = new HashMap<>();

        for (ProductResponse p : products) {
            try {
                List<ProductImageResponse> productImages = productImageClient.list(p.id());

                ProductImageResponse main = productImages.stream()
                        .filter(img -> Boolean.TRUE.equals(img.isMain()))
                        .findFirst()
                        .orElse(productImages.isEmpty() ? null : productImages.get(0));

                imageMap.put(
                        p.id(),
                        (main != null && main.imageUrl() != null && !main.imageUrl().isBlank())
                                ? main.imageUrl()
                                : "/images/placeholder.png"
                );
            } catch (Exception ex) {
                imageMap.put(p.id(), "/images/placeholder.png");
            }
        }

        return imageMap;
    }
}