package com.school.ppmg.computer_equipment_store_system_client.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    private String deliveryName;
    private String deliveryPhone;
    private String country;
    private String city;
    private String postalCode;
    private String street;
    private String streetNumber;
}