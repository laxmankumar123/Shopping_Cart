package com.ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.ecom")


//@SpringBootApplication
@EntityScan(basePackages = "com.ecom.model") // Ensure it points to the package of your entity classes
@EnableJpaRepositories(basePackages = "com.ecom.repository")
public class ShoppingCartApplication {
     //
	public static void main(String[] args) {
		SpringApplication.run(ShoppingCartApplication.class, args);
	}

}
