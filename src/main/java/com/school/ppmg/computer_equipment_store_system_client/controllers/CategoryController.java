package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.CategoryClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.ApiConflictException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryClient categoryClient;

    @GetMapping("/categories")
    public String categoriesPage(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            Model model
    ) {
        if (sort == null || sort.isBlank()) {
            sort = "name,asc";
        }

        PageResponse<CategoryResponse> result =
                categoryClient.getAll(q, isActive, page, size, sort);

        model.addAttribute("page", result);
        model.addAttribute("categories", result.getContent());

        model.addAttribute("q", q);
        model.addAttribute("isActive", isActive);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        return "categories/list-categories";
    }
    @GetMapping("/add-category")
    public String createCategory(Model model) {
        model.addAttribute("category", new CategoryRequest());
        return "categories/create-category";
    }

    @PostMapping("/save-category")
    public String submitCategory(@Valid @ModelAttribute("category") CategoryRequest request,
                                 BindingResult bindingResult,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            return "categories/create-category";
        }

        try {
            categoryClient.create(request);
            return "redirect:/categories";
        } catch (ApiConflictException ex) {
            // глобално съобщение във формата
            bindingResult.addError(new ObjectError("category", ex.getMessage()));
            return "categories/create-category";
        }
    }
    @GetMapping("/edit-category/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        CategoryResponse c = categoryClient.getById(id);

        CategoryRequest req = new CategoryRequest();
        req.setName(c.name());
        req.setSlug(c.slug());
        req.setIsActive(c.isActive());

        model.addAttribute("category", req);
        model.addAttribute("categoryId", id);

        return "categories/edit-category";
    }

    @PostMapping("/edit-category/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("category") CategoryRequest request,
                                 BindingResult bindingResult,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryId", id);
            return "categories/edit-category";
        }

        try {
            categoryClient.update(id, request);
            return "redirect:/categories";
        } catch (ApiConflictException ex) {
            // ако slug / name вече съществува
            bindingResult.rejectValue("slug", "conflict", ex.getMessage());
            model.addAttribute("categoryId", id);
            return "categories/edit-category";
        }
    }
    @PostMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryClient.delete(id);
        return "redirect:/categories";
    }

}
