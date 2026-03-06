package com.school.ppmg.computer_equipment_store_system_client.dtos.order;

import com.school.ppmg.computer_equipment_store_system_client.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {

    private OrderStatus status;


}