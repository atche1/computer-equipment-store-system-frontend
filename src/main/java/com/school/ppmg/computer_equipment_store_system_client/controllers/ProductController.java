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
            Model model
    ) {
        if (sort == null || sort.isBlank()) {
            sort = "name,asc";
        }

        PageResponse<ProductResponse> result =
                productClient.getAll(q, categoryId, isActive, minPrice, maxPrice, inStock, page, size, sort);

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

        return "products/list-products";
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