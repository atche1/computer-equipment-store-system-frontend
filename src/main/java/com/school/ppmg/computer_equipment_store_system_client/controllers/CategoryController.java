package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.CategoryClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
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

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryClient categoryClient;
    private final AdminGuard adminGuard;

    @GetMapping("/categories")
    public String categoriesPage(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            HttpSession session,
            Model model
    ) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

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

        return "admin/categories/list-categories";
    }

    @GetMapping("/add-category")
    public String createCategory(HttpSession session, Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        model.addAttribute("category", new CategoryRequest());
        return "admin/categories/create-category";
    }

    @PostMapping("/save-category")
    public String submitCategory(@Valid @ModelAttribute("category") CategoryRequest request,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        if (bindingResult.hasErrors()) {
            return "admin/categories/create-category";
        }

        try {
            categoryClient.create(request);
            return "redirect:/categories";
        } catch (BackendException ex) {
            bindingResult.addError(new ObjectError("category", ex.getMessage()));
            return "admin/categories/create-category";
        }
    }

    @GetMapping("/edit-category/{id}")
    public String editCategory(@PathVariable Long id, HttpSession session, Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        CategoryResponse c = categoryClient.getById(id);

        CategoryRequest req = new CategoryRequest();
        req.setName(c.name());
        req.setSlug(c.slug());
        req.setIsActive(c.isActive());

        model.addAttribute("category", req);
        model.addAttribute("categoryId", id);

        return "admin/categories/edit-category";
    }

    @PostMapping("/edit-category/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("category") CategoryRequest request,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryId", id);
            return "admin/categories/edit-category";
        }

        try {
            categoryClient.update(id, request);
            return "redirect:/categories";
        } catch (BackendException ex) {
            bindingResult.rejectValue("slug", "conflict", ex.getMessage());
            model.addAttribute("categoryId", id);
            return "admin/categories/edit-category";
        }
    }

    @PostMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable Long id, HttpSession session) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        categoryClient.delete(id);
        return "redirect:/categories";
    }
}