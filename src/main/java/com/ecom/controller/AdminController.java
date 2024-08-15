package com.ecom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecom.model.Category;
import com.ecom.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	
	@Autowired
	private CategoryService categoryService;
	@GetMapping("/")
	public String index() {
		return "admin/index";
	}
	
	
	
	@GetMapping("/loadAddProduct")
	public String loadAddProduct() {
		return "admin/add_product";
	}
	
	
	
	@GetMapping("/category")
	public String category() {
		return "admin/category";
	}
	
	

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, HttpSession session) {
		Boolean existCategory= categoryService.existCategory(category.getName());
		
		
		
		categoryService.saveCategory(category);
		return "redirect:/category";
	}
	
	

}
