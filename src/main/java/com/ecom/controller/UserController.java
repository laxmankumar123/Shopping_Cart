package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecom.model.Category;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.UserServic;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private UserServic userServic;
	
	
	public String home() {
		
		return "user/home";
	}
	
	@ModelAttribute
	public void getUserDetails(Principal p, org.springframework.ui.Model m) {
		if(p!=null) {
			
			String email=p.getName();
			UserDtls userByEmail = userServic.getUserByEmail(email);
			m.addAttribute("user", userByEmail);
		}
		
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

}
