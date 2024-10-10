package com.ecom.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

import jakarta.mail.MessagingException;

public interface OrderService {
	
	public void saveOrder(Integer userid,OrderRequest orderRequest) throws UnsupportedEncodingException, MessagingException;
	
	public List<ProductOrder> getOrderByUser(Integer userId);
	
	public ProductOrder updateOrderStatus(Integer id,String status);
	
	public List<ProductOrder> getAllOrders();
	
	

}
