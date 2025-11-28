package br.com.challenge.weight_processor_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeightProcessorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeightProcessorServiceApplication.class, args);
	}

}
