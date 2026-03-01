package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.CategoryAttributeClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductAttributeValueClient;
import com.school.ppmg.computer_equipment_store_system_client.clients.ProductClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.attribute.AttributeResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product.ProductResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product_attribute_value.ProductAttributeValueRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.product_attribute_value.ProductAttributeValueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ProductAttributeValueController {

    private final ProductClient productClient;
    private final CategoryAttributeClient categoryAttributeClient;
    private final ProductAttributeValueClient pavClient;

    @GetMapping("/products/{productId}/attribute-values")
    public String manage(@PathVariable Long productId, Model model) {
        ProductResponse product = productClient.getById(productId);

        // атрибутите за категорията
        List<AttributeResponse> categoryAttributes = categoryAttributeClient.list(product.categoryId());

        // текущите стойности на продукта
        List<ProductAttributeValueResponse> current = pavClient.list(productId);
        Map<Long, ProductAttributeValueResponse> byAttrId = new HashMap<>();
        for (var v : current) byAttrId.put(v.attributeId(), v);

        // подготвяме редове за view
        List<Row> rows = new ArrayList<>();
        for (AttributeResponse a : categoryAttributes) {
            ProductAttributeValueResponse existing = byAttrId.get(a.id());

            Row r = new Row();
            r.setAttributeId(a.id());
            r.setName(a.name());
            r.setDataType(a.dataType().name());
            r.setUnit(a.unit());

            if (existing != null) {
                r.setValueText(existing.valueText());
                r.setValueNumber(existing.valueNumber());
                r.setValueBoolean(existing.valueBoolean());
            }
            rows.add(r);
        }

        model.addAttribute("product", product);
        model.addAttribute("productId", productId);
        model.addAttribute("rows", rows);

        return "product_attribute_values/product-attribute-values";
    }

    @PostMapping("/products/{productId}/attribute-values/save")
    public String save(
            @PathVariable Long productId,
            @RequestParam Map<String, String> params,
            RedirectAttributes ra
    ) {
        // Четем редовете от params: attrId_1, type_1, text_1, number_1, bool_1
        List<ProductAttributeValueRequest> requests = new ArrayList<>();

        // намираме всички attrId_* ключове
        params.forEach((k, v) -> {
            if (k.startsWith("attrId_")) {
                String idx = k.substring("attrId_".length());
                Long attributeId = Long.valueOf(v);

                String type = params.get("type_" + idx);

                String text = params.get("text_" + idx);
                String numberStr = params.get("number_" + idx);
                String boolStr = params.get("bool_" + idx); // "on" ако е чекнат

                String valueText = null;
                BigDecimal valueNumber = null;
                Boolean valueBoolean = null;

                if ("TEXT".equals(type)) {
                    valueText = (text == null || text.isBlank()) ? null : text.trim();
                } else if ("NUMBER".equals(type)) {
                    if (numberStr != null && !numberStr.isBlank()) {
                        valueNumber = new BigDecimal(numberStr.trim());
                    }
                } else if ("BOOLEAN".equals(type)) {
                    valueBoolean = "on".equalsIgnoreCase(boolStr);
                }

                requests.add(new ProductAttributeValueRequest(attributeId, valueText, valueNumber, valueBoolean));
            }
        });

        try {
            pavClient.upsertBatch(productId, requests);
            ra.addFlashAttribute("successMessage", "Saved successfully.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/products/" + productId + "/attribute-values";
    }

    // Simple view model
    @lombok.Data
    public static class Row {
        private Long attributeId;
        private String name;
        private String dataType; // TEXT/NUMBER/BOOLEAN
        private String unit;

        private String valueText;
        private BigDecimal valueNumber;
        private Boolean valueBoolean;
    }
}