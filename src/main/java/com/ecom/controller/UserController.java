package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.UserDtls;
import com.ecom.repository.CartRepository;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.UserServic;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private UserServic userServic;
	
	@Autowired
	private CartService cartService;
	
	public String home() {
		
		return "user/home";
	}
	
	@ModelAttribute
	public void getUserDetails(Principal p, org.springframework.ui.Model m) {
		if(p!=null) {
			
			String email=p.getName();
			UserDtls userByEmail = userServic.getUserByEmail(email);
			m.addAttribute("user", userByEmail);
			
			Integer countCart = cartService.getCountCart(userByEmail.getId());
			m.addAttribute("countCart", countCart);
		}
		
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}
	
	
	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		 Cart saveCart = cartService.saveCart(pid, uid);
		 System.out.println("=====pid"+pid);
		 System.out.println("=====uid"+uid);
		 if(ObjectUtils.isEmpty(saveCart)) {
			 System.out.println("---added");
			 session.setAttribute("errorMsg", "Product add to cart is Field.....!!!");
		 }else {
			 System.out.println("---Insert empty");
			session.setAttribute("succMsg", "Product added to cart");
		}
		 return "redirect:/product/" + pid;
	}
	
	
	@GetMapping("/cart")
	public String loadCartPage( Principal p, org.springframework.ui.Model m) {
		
		UserDtls user=getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		return "user/cart";
		}

	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userByEmail = userServic.getUserByEmail(email);
		return userByEmail;
	}
	
	
	
	

}
