package com.ecom.util;

import java.io.UnsupportedEncodingException;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ecom.model.ProductOrder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private  JavaMailSender mailSender;

	public Boolean sendMail(String url, String recipientEmail)
			throws UnsupportedEncodingException, MessagingException {
		System.out.println("==recipient"+recipientEmail);
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		

		helper.setFrom("laxman@strategicerp.com", "shopping cart");
		helper.setTo(recipientEmail);
		
		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
				+ "\">Change my password</a></p>";
	
		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {
		String siteUrl = request.getRequestURL().toString();
		return siteUrl.replaceFirst(request.getServletPath(), "");

	}
	
	String msg =
			"<p>[[name]]</p> <b> <p>Thank you for order ...  [[orderStatus]] </b> </p>"
			+ "<p> <br> Product Details </br>: </p>"
			+ "<p>Name: [[productName]] </p>"
			+ "<p> Category: [[category]]</p>"
			+ "<p> Quantity : [[quantity]]</p>"
			+ "<p> Price : [[price]]</p>"
			+ "<p> Pyment Type : [[paymentType]]</p>";
	
	
	public Boolean sendMailForProductOrder(ProductOrder order, String status)
			throws UnsupportedEncodingException, MessagingException {
		
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		

		helper.setFrom("laxman@strategicerp.com", "shopping cart");
		helper.setTo(order.getOrderAddress().getEmail());
		
		
		msg=msg.replace("[[name]]", order.getOrderAddress().getFirstName());
		msg=msg.replace("[[orderStatus]]", status);
		msg=msg.replace("[[productName]]", order.getProduct().getTitle());
		msg=msg.replace("[[category]]", order.getProduct().getCategory());
		msg=msg.replace("[[quantity]]", order.getQuantity().toString());
		//msg=msg.replace("[[price]]", order.getPrice());
		msg=msg.replace("[[paymentType]]", order.getPaymentType());
		
		
	
		helper.setSubject("Product Order status");
		helper.setText(msg, true);
		mailSender.send(message);
		return true;
	}
	
	
}
