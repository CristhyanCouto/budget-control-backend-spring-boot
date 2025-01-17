package com.budget.control.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BudgetControlApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(BudgetControlApiApplication.class, args);
	}

}
