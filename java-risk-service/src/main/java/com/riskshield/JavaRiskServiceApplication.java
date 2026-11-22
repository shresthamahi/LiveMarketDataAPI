package com.riskshield;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JavaRiskServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaRiskServiceApplication.class, args);
	}

}
