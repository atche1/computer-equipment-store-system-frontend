package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.CategoryClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product.ProductRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product.ProductResponse;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.ApiConflictException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductClient productClient;
    private final CategoryClient categoryClient;
    private final com.school.ppmg.computer_equipment_store_system_client.clients.CategoryAttributeClient categoryAttributeClient;

    @GetMapping("/products")
    public String productsPage(
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
            Model model
    ) {
        if (sort == null || sort.isBlank()) {
            sort = "name,asc";
        }

        // 1) Load filterable attributes for selected category
        List<com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeResponse> filterableAttributes = List.of();
        if (categoryId != null) {
            filterableAttributes = categoryAttributeClient.list(categoryId).stream()
                    .filter(a -> Boolean.TRUE.equals(a.isFilterable()))
                    .toList();
        }

        // 2) Parse attr filters from request params:
        // attrText_12=Logitech -> "12:Logitech"
        List<String> attrText = new java.util.ArrayList<>();
        List<String> attrNumMin = new java.util.ArrayList<>();
        List<String> attrNumMax = new java.util.ArrayList<>();
        List<String> attrBool = new java.util.ArrayList<>();

        java.util.Map<Long, String> uiText = new java.util.HashMap<>();
        java.util.Map<Long, String> uiNumMin = new java.util.HashMap<>();
        java.util.Map<Long, String> uiNumMax = new java.util.HashMap<>();
        java.util.Map<Long, String> uiBool = new java.util.HashMap<>();

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

        // 3) Call backend
        PageResponse<ProductResponse> result =
                productClient.getAll(q, categoryId, isActive, minPrice, maxPrice, inStock,
                        attrText.isEmpty() ? null : attrText,
                        attrNumMin.isEmpty() ? null : attrNumMin,
                        attrNumMax.isEmpty() ? null : attrNumMax,
                        attrBool.isEmpty() ? null : attrBool,
                        page, size, sort);

        List<CategoryResponse> categories = categoryClient.listActive();

        model.addAttribute("page", result);
        model.addAttribute("products", result.getContent());
        model.addAttribute("categories", categories);

        model.addAttribute("q", q);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("isActive", isActive);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStock", inStock);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        // attribute filters for UI
        model.addAttribute("filterableAttributes", filterableAttributes);
        model.addAttribute("uiText", uiText);
        model.addAttribute("uiNumMin", uiNumMin);
        model.addAttribute("uiNumMax", uiNumMax);
        model.addAttribute("uiBool", uiBool);

        // 4) Build queryString (without page) for pager links
        model.addAttribute("queryString", buildQueryString(params, sort, size));

        return "products/list-products";
    }

    private Long tryParseIdSuffix(String key, String prefix) {
        try {
            return Long.parseLong(key.substring(prefix.length()));
        } catch (Exception ex) {
            return null;
        }
    }

    private String buildQueryString(org.springframework.util.MultiValueMap<String, String> params, String sort, int size) {
        // keep all current params except "page"; ensure sort/size exist
        java.util.Map<String, String> flat = new java.util.LinkedHashMap<>();

        for (var e : params.entrySet()) {
            String k = e.getKey();
            if ("page".equals(k)) continue;
            if (e.getValue() == null || e.getValue().isEmpty()) continue;
            String v = e.getValue().get(0);
            if (v == null || v.isBlank()) continue;
            flat.put(k, v);
        }

        if (!flat.containsKey("sort")) flat.put("sort", sort);
        if (!flat.containsKey("size")) flat.put("size", String.valueOf(size));

        StringBuilder sb = new StringBuilder();
        for (var e : flat.entrySet()) {
            sb.append("&").append(urlEnc(e.getKey())).append("=").append(urlEnc(e.getValue()));
        }
        return sb.toString();
    }

    private String urlEnc(String s) {
        try {
            return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return s;
        }
    }

    @GetMapping("/add-product")
    public String createProduct(Model model) {
        model.addAttribute("product", new ProductRequest());
        model.addAttribute("categories", categoryClient.listActive());
        return "products/create-product";
    }

    @PostMapping("/save-product")
    public String submitProduct(@Valid @ModelAttribute("product") ProductRequest request,
                                BindingResult bindingResult,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryClient.listActive());
            return "products/create-product";
        }

        try {
            productClient.create(request);
            return "redirect:/products";
        } catch (ApiConflictException ex) {
            bindingResult.addError(new ObjectError("product", ex.getMessage()));
            model.addAttribute("categories", categoryClient.listActive());
            return "products/create-product";
        }
    }

    @GetMapping("/edit-product/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
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

        return "products/edit-product";
    }

    @PostMapping("/edit-product/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("product") ProductRequest request,
                                BindingResult bindingResult,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryClient.listActive());
            return "products/edit-product";
        }

        try {
            productClient.update(id, request);
            return "redirect:/products";
        } catch (ApiConflictException ex) {
            bindingResult.addError(new ObjectError("product", ex.getMessage()));
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryClient.listActive());
            return "products/edit-product";
        }
    }

    @PostMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productClient.delete(id);
        return "redirect:/products";
    }
}