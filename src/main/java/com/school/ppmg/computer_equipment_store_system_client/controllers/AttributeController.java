package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.AttributeClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.common.PageResponse;
import com.school.ppmg.computer_equipment_store_system_client.enums.AttributeDataType;
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
public class AttributeController {

    private final AttributeClient attributeClient;
    private final AdminGuard adminGuard;

    @GetMapping("/attributes")
    public String attributesPage(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) AttributeDataType dataType,
            @RequestParam(required = false) Boolean filterable,
            @RequestParam(required = false) String unit,
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

        PageResponse<AttributeResponse> result =
                attributeClient.getAll(q, dataType, filterable, unit, page, size, sort);

        model.addAttribute("page", result);
        model.addAttribute("attributes", result.getContent());
        model.addAttribute("q", q);
        model.addAttribute("dataType", dataType);
        model.addAttribute("filterable", filterable);
        model.addAttribute("unit", unit);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);
        model.addAttribute("dataTypes", AttributeDataType.values());

        return "admin/attributes/list-attributes";
    }

    @GetMapping("/add-attribute")
    public String createAttribute(HttpSession session, Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        AttributeRequest req = new AttributeRequest();
        req.setDataType(AttributeDataType.TEXT);
        if (req.getIsFilterable() == null) {
            req.setIsFilterable(true);
        }

        model.addAttribute("attribute", req);
        model.addAttribute("dataTypes", AttributeDataType.values());

        return "admin/attributes/create-attribute";
    }

    @PostMapping("/save-attribute")
    public String submitAttribute(@Valid @ModelAttribute("attribute") AttributeRequest request,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        if (bindingResult.hasErrors()) {
            model.addAttribute("dataTypes", AttributeDataType.values());
            return "admin/attributes/create-attribute";
        }

        try {
            attributeClient.create(request);
            return "redirect:/attributes";
        } catch (BackendException ex) {
            bindingResult.addError(new ObjectError("attribute", ex.getMessage()));
            model.addAttribute("dataTypes", AttributeDataType.values());
            return "admin/attributes/create-attribute";
        }
    }

    @GetMapping("/edit-attribute/{id}")
    public String editAttribute(@PathVariable Long id, HttpSession session, Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        AttributeResponse a = attributeClient.getById(id);

        AttributeRequest req = new AttributeRequest();
        req.setName(a.name());
        req.setDataType(a.dataType());
        req.setUnit(a.unit());
        req.setIsFilterable(a.isFilterable());

        model.addAttribute("attribute", req);
        model.addAttribute("attributeId", id);
        model.addAttribute("dataTypes", AttributeDataType.values());

        return "admin/attributes/edit-attribute";
    }

    @PostMapping("/edit-attribute/{id}")
    public String updateAttribute(@PathVariable Long id,
                                  @Valid @ModelAttribute("attribute") AttributeRequest request,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        if (bindingResult.hasErrors()) {
            model.addAttribute("attributeId", id);
            model.addAttribute("dataTypes", AttributeDataType.values());
            return "admin/attributes/edit-attribute";
        }

        try {
            attributeClient.update(id, request);
            return "redirect:/attributes";
        } catch (BackendException ex) {
            bindingResult.rejectValue("name", "conflict", ex.getMessage());
            model.addAttribute("attributeId", id);
            model.addAttribute("dataTypes", AttributeDataType.values());
            return "admin/attributes/edit-attribute";
        }
    }

    @PostMapping("/delete-attribute/{id}")
    public String deleteAttribute(@PathVariable Long id, HttpSession session) {
        String redirect = adminGuard.check(session);
        if (redirect != null) return redirect;

        attributeClient.delete(id);
        return "redirect:/attributes";
    }
}