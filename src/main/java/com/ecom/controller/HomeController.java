package com.ecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class HomeController {
	
	@GetMapping("/")
	public String index() {
		return "index.html";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login.html";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register.html";
	}
	
	@GetMapping("/products")
	public String products(Model m) {
		return "product.html";
	}
	
	@GetMapping("/product")
	public String product() {
		return "veiw_product.html";
	}

}
