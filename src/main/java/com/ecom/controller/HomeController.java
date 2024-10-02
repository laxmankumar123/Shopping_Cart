package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserServic;
import com.ecom.util.CommonUtil;

import ch.qos.logback.core.model.Model;
import jakarta.mail.MessagingException;
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
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private CartService cartService;
	
	
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
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException  {
		
		UserDtls userByEmail = userServic.getUserByEmail(email);
		
		if(ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg", "Invalid Email");
		}else {
			
			String resetToken = UUID.randomUUID().toString();
			userServic.updateUserResetToken(email, resetToken);
			
			System.out.println("-------resetToken"+resetToken);

			// Generate url 
			//http://localhost:8080/reset-password?token=e62e05b5-6c3c-4234-b5e4-ef66bdf91ec7

			String url = CommonUtil.generateUrl(request)+"/reset-password?token="+resetToken;
			
			System.out.println("====email"+email);
			System.out.println("====url"+url);
			
			
			Boolean sendMail = commonUtil.sendMail(url, email);
			
			if(sendMail) {
				session.setAttribute("succMsg", "Please check your email...  Password reset link sent ");
			}else {
				session.setAttribute("errorMsg", "Somthing went wrong on server ! Email not sent ");
			}
		}
		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, org.springframework.ui.Model m) {
		
		System.out.println("----token"+token);
		UserDtls userByToken = userServic.getUserByToken(token);
		System.out.println("----userByToken"+userByToken);
		
		
		if(userByToken==null) {
			m.addAttribute("msg", "Your link is invalid or Expired");
			return "message";
		}
		m.addAttribute("token", token);
		return "reset_password";
	}
	
	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token, @RequestParam String password, org.springframework.ui.Model m, HttpSession session) {

		UserDtls userByToken = userServic.getUserByToken(token);

		if(userByToken==null) {
			m.addAttribute("errorMsg", "Your link is invalid or Expired");
			return "message";
			
		}else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userServic.updateUser(userByToken);
			//session.setAttribute("msg", "Password changed succussfully");
			m.addAttribute("msg","Password change successfully");
			return "message";
		}
		
		
	}
	
	
	

}
