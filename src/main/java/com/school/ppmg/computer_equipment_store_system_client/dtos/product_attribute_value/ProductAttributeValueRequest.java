package com.school.ppmg.computer_equipment_store_system_client.dtos.product_attribute_value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeValueRequest {
    private Long attributeId;
    private String valueText;
    private BigDecimal valueNumber;
    private Boolean valueBoolean;
}
