package com.school.ppmg.computer_equipment_store_system_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.school.ppmg.computer_equipment_store_system_client.client")
public class ComputerEquipmentStoreSystemClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComputerEquipmentStoreSystemClientApplication.class, args);
	}

}
