package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.CategoryClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryClient categoryClient;

    @GetMapping("/categories")
    public String categoriesPage(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            Model model
    ) {
        if (sort == null || sort.isBlank()) {
            sort = "name,asc";
        }

        PageResponse<CategoryResponse> result =
                categoryClient.search(q, page, size, sort);

        model.addAttribute("page", result);
        model.addAttribute("categories", result.getContent());

        // keep query params for pagination & filters
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        return "categories/index";
    }

}
