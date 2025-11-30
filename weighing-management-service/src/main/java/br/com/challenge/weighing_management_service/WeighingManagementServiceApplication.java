package br.com.challenge.weighing_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class WeighingManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeighingManagementServiceApplication.class, args);
	}

}
