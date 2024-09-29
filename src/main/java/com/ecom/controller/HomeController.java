package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserServic;
import com.ecom.util.CommonUtil;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	
	
	@Autowired
	private CategoryService categoryService;
	
	
	@Autowired
	private ProductService productService;
	
	
	@Autowired
	private UserServic userServic;
	
	
	
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
	
	
	@GetMapping("/")
	public String index() {
		return "index.html";
	}
	
	@GetMapping("/signin")
	public String login() {
		return "login.html";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@GetMapping("/products")
	public String products(org.springframework.ui.Model m,  @RequestParam(value = "category", defaultValue = "") String category) {
		
		List<Category> categories = categoryService.getAllActiveCategory();
		List<Product> products = productService.getAllActiveProducts(category);
		
		
		m.addAttribute("categories", categories);
		m.addAttribute("products", products);
		m.addAttribute("paramValue", category);
		
		return "product";
	}
	
	@GetMapping("/product/{id}")
	public String product(@PathVariable int id, org.springframework.ui.Model m) {
		Product productById = productService.getProductById(id);
		m.addAttribute("product", productById);
		
		System.out.println("=============id"  +   id);
		
			
	
		
		
		return "veiw_product";
	}

	
	
	
	@GetMapping("/product")
	public String product() {
		return "veiw_product.html";
	}
	
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file,HttpSession session ) throws IOException {
		
		String imageName=file.isEmpty()? "default.jpg":file.getOriginalFilename();
		user.setProfileImage(imageName);
		UserDtls saveUser = userServic.saveUser(user);
		
		if(!ObjectUtils.isEmpty(saveUser)) {
			
			if(!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());

				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				

			}
			session.setAttribute("succMsg", "Saved successfully");
			
				
				
			}else {
				session.setAttribute("errorMsg", "Not saved ! internal server error");
		}
		return "redirect:/register";
	}
	
	@GetMapping("/forgot-password")
	public String showForgotPassword() {
		return "forgot_password.html";
	}
	
	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) {
		
		UserDtls userByEmail = userServic.getUserByEmail(email);
		
		if(ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg", "Invalid Email");
		}else {
			
			String resetToken = UUID.randomUUID().toString();
			userServic.updateUserResetToken(email, resetToken);
			System.out.println("-------resetToken"+resetToken);
			
			
			// Generate url http://localhost:8080/forgot-password?token=efjfhhvjdfbjvdbbjbmkbmk
			
			
			String url = CommonUtil.generateUrl(request);
			
			
			
			
			
			Boolean sendMail = CommonUtil.sendMail();
			if(sendMail) {
				session.setAttribute("succMsg", "Please check your email...  Password reset link sent ");
			}else {
				session.setAttribute("errorMsg", "Somthing went wrong on server ! Email not sent ");
			}
		}
		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPassword() {
		return "reset_password.html";
	}
	
	

}
