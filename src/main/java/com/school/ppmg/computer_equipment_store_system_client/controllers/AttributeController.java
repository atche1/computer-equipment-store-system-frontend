package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.AttributeClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import com.school.ppmg.computer_equipment_store_system_client.enums.AttributeDataType;
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
public class AttributeController {
    private final AttributeClient attributeClient;
    @GetMapping("/attributes")
    public String attributesPage(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) AttributeDataType dataType,
            @RequestParam(required = false) Boolean filterable,
            @RequestParam(required = false) String unit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            Model model
    ) {
        if (sort == null || sort.isBlank()) {
            sort = "name,asc";
        }

        PageResponse<AttributeResponse> result =
                attributeClient.getAll(q, dataType, filterable, unit, page, size, sort);

        model.addAttribute("page", result);
        model.addAttribute("attributes", result.getContent());

        // за да се запазят филтрите във view-то
        model.addAttribute("q", q);
        model.addAttribute("dataType", dataType);
        model.addAttribute("filterable", filterable);
        model.addAttribute("unit", unit);

        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        // dropdown за enum
        model.addAttribute("dataTypes", AttributeDataType.values());

        return "attributes/list-attributes";
    }


    @GetMapping("/add-attribute")
    public String createAttribute(Model model) {
        AttributeRequest req = new AttributeRequest();
        // default стойност за dropdown-а (по желание)
        req.setDataType(AttributeDataType.TEXT);
        // default filterable (ако в request е null)
        if (req.getIsFilterable() == null) {
            req.setIsFilterable(true);
        }

        model.addAttribute("attribute", req);
        model.addAttribute("dataTypes", AttributeDataType.values());
        return "attributes/create-attribute";
    }


    @PostMapping("/save-attribute")
    public String submitAttribute(
            @Valid @ModelAttribute("attribute") AttributeRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("dataTypes", AttributeDataType.values());
            return "attributes/create-attribute";
        }

        try {
            attributeClient.create(request);
            return "redirect:/attributes";
        } catch (ApiConflictException ex) {
            bindingResult.addError(new ObjectError("attribute", ex.getMessage()));
            model.addAttribute("dataTypes", AttributeDataType.values());
            return "attributes/create-attribute";
        }
    }


    @GetMapping("/edit-attribute/{id}")
    public String editAttribute(@PathVariable Long id, Model model) {
        AttributeResponse a = attributeClient.getById(id);

        AttributeRequest req = new AttributeRequest();
        req.setName(a.name());
        req.setDataType(a.dataType());
        req.setUnit(a.unit());
        req.setIsFilterable(a.isFilterable());

        model.addAttribute("attribute", req);
        model.addAttribute("attributeId", id);
        model.addAttribute("dataTypes", AttributeDataType.values());

        return "attributes/edit-attribute";
    }


    @PostMapping("/edit-attribute/{id}")
    public String updateAttribute(
            @PathVariable Long id,
            @Valid @ModelAttribute("attribute") AttributeRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("attributeId", id);
            model.addAttribute("dataTypes", AttributeDataType.values());
            return "attributes/edit-attribute";
        }

        try {
            attributeClient.update(id, request);
            return "redirect:/attributes";
        } catch (ApiConflictException ex) {
            // конфликт (например: име вече съществува)
            bindingResult.rejectValue("name", "conflict", ex.getMessage());
            model.addAttribute("attributeId", id);
            model.addAttribute("dataTypes", AttributeDataType.values());
            return "attributes/edit-attribute";
        }
    }


    @PostMapping("/delete-attribute/{id}")
    public String deleteAttribute(@PathVariable Long id) {
        attributeClient.delete(id);
        return "redirect:/attributes";
    }
}
