package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.AttributeClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.CategoryAttributeClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.CategoryClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category.CategoryResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.category_attribute.CategoryAttributeAddRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CategoryAttributeController {

    private final CategoryClient categoryClient;
    private final AttributeClient attributeClient;
    private final CategoryAttributeClient categoryAttributeClient;

    @GetMapping("/categories/{categoryId}/attributes")
    public String manage(@PathVariable Long categoryId, Model model) {
        CategoryResponse category = categoryClient.getById(categoryId);

        List<AttributeResponse> assigned = categoryAttributeClient.list(categoryId);

        // всички атрибути за dropdown (увеличи size ако имаш много)
        PageResponse<AttributeResponse> allPage =
                attributeClient.getAll(null, null, null, null, 0, 1000, "name,asc");

        List<AttributeResponse> all = (allPage != null && allPage.getContent() != null)
                ? allPage.getContent()
                : List.of();

        Set<Long> assignedIds = assigned.stream()
                .map(AttributeResponse::id)
                .collect(Collectors.toSet());

        List<AttributeResponse> available = all.stream()
                .filter(a -> a.id() != null && !assignedIds.contains(a.id()))
                .toList();

        model.addAttribute("category", category);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("assignedAttributes", assigned);
        model.addAttribute("availableAttributes", available);

        // ако решиш да ползваш th:object в бъдеще (не е задължително)
        model.addAttribute("addRequest", new CategoryAttributeAddRequest());

        return "category_attributes/category-attributes";
    }

    @PostMapping("/categories/{categoryId}/attributes/add")
    public String add(
            @PathVariable Long categoryId,
            @RequestParam Long attributeId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            CategoryAttributeAddRequest req = new CategoryAttributeAddRequest(attributeId);
            categoryAttributeClient.add(categoryId, req);
            redirectAttributes.addFlashAttribute("successMessage", "Attribute added successfully.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/categories/" + categoryId + "/attributes";
    }

    @PostMapping("/categories/{categoryId}/attributes/remove/{attributeId}")
    public String remove(
            @PathVariable Long categoryId,
            @PathVariable Long attributeId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            categoryAttributeClient.remove(categoryId, attributeId);
            redirectAttributes.addFlashAttribute("successMessage", "Attribute removed successfully.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/categories/" + categoryId + "/attributes";
    }
}