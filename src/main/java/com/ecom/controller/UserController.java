package com.ecom.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserServic;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;
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
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CommonUtil commonUtil;

	
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
		if(carts.size()>0) {
			double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "user/cart";
		}
	
	
	
	@GetMapping("/cartQuantityUpdate")
	public String cartQuantityUpdate(@RequestParam String sy, @RequestParam Integer cid) {
				
		cartService.updateQuantity(sy,cid);
		
		return "redirect:/user/cart";
	}
	
	

	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userByEmail = userServic.getUserByEmail(email);		
		return userByEmail;
	}
	
	
	@GetMapping("/orders")
	public String oorderPage( Principal p, org.springframework.ui.Model m) {
		UserDtls user=getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		if(carts.size()>0) {
			double orderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
			double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice()+250+100;
			
			m.addAttribute("orderPrice", orderPrice);
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/order";
	}
	
	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p) throws UnsupportedEncodingException, MessagingException {
		System.out.println(request);
		UserDtls user = getLoggedInUserDetails(p);
		
		orderService.saveOrder(user.getId(), request);

		return "redirect:/user/success";
	}
	
	@GetMapping("/success")
	public String loadSuccess() {
		return "/user/success";
	}
	
	
	@GetMapping("/user-orders")
	public String myOrder(org.springframework.ui.Model m, Principal p) {
		
		UserDtls loginUser = getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrderByUser(loginUser.getId());
		m.addAttribute("orders", orders);
		return "/user/my_orders";
	}
	
	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam String st, HttpSession session) {
		
		OrderStatus[] values = OrderStatus.values();
		String status= null;
		System.out.println("---st--"+st);
		for (OrderStatus orderSt : values) {
			int number = Integer.parseInt(st);
			if (orderSt.getId()==number) {
				status = orderSt.getName();
				System.out.println("---status inside--"+status);
			}
		}
		
		System.out.println("---status"+status);
		
		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
		try {
			commonUtil.sendMailForProductOrder(updateOrder, status);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Status updated");
			
		}else {
			session.setAttribute("errorMsg", "status not updated");
		}
		
		return "redirect:/user/user-orders";
	}
	
	
	
	
	
	

}
