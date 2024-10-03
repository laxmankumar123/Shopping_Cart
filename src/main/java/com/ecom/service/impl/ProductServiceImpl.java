package com.ecom.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	
	@Override
	public Product saveProduct(Product product) {
		
		return productRepository.save(product);
	}


	@Override
	public List<Product> getAllProducts() {
		
		return productRepository.findAll();
	}


	@Override
	public Boolean deleteProduct(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		//
		if(!ObjectUtils.isEmpty(product)) {
			productRepository.delete(product);
			return true;
		
			
		}
		return false;
	}


	@Override
	public Product getProductById(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		return product;
	}



	@Override
	public Product updateProduct(Product product, MultipartFile image) {
		Product dbProduct = getProductById(product.getId());
		String imageNmae=image.isEmpty() ? dbProduct.getImage():image.getOriginalFilename();
		//dbProduct.setImage(imageNmae);
		dbProduct.setTitle(product.getTitle());;
		dbProduct.setDescription(product.getDescription());
		dbProduct.setCategory(product.getCategory());
		dbProduct.setPrice(product.getPrice());
		dbProduct.setStock(product.getStock());
		dbProduct.setImage(imageNmae);
		dbProduct.setIsActive(product.getIsActive());
		dbProduct.setDiscount(product.getDiscount());
		//5=100*(5/100);100-5=95
		Double discount = product.getPrice()*product.getDiscount()/100;	
		Double discountPrice= product.getPrice() - discount;		
		dbProduct.setDiscountPrice(discountPrice);
		System.out.println("---------"+product.getPrice()*product.getDiscount()/100);
		System.out.println("----product.getPrice()"+product.getPrice());
		System.out.println("----product.getDiscount()"+product.getDiscount());
		System.out.println("----discount"+discount);
		System.out.println("----discountPrice"+discountPrice);
		Product updateProduct = productRepository.save(dbProduct);
		if(!ObjectUtils.isEmpty(updateProduct)) {
			if(!image.isEmpty()) {		
				try {
				File saveFile = new ClassPathResource("static/img/").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
						+ image.getOriginalFilename());
				System.out.println("=====path" + path);
				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("----files--"+image.getInputStream());
				}catch (Exception e) {
					e.printStackTrace();
				}				
			}
			return product;
		}
		return null;
		} 
	/*
	
	
	@Override
	public Product updateProduct(Product product, MultipartFile image) {
	    Product dbProduct = getProductById(product.getId());

	    // Set image name
	    String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();
	    dbProduct.setImage(imageName);

	    // Update product details
	    dbProduct.setTitle(product.getTitle());
	    dbProduct.setDescription(product.getDescription());
	    dbProduct.setCategory(product.getCategory());
	    dbProduct.setPrice(product.getPrice());
	    dbProduct.setStock(product.getStock());
	    dbProduct.setIsActive(product.getIsActive());
	    dbProduct.setDiscount(product.getDiscount());

	    // Calculate discounted price
	    Double discount = product.getPrice() * product.getDiscount() / 100;
	    Double discountPrice = product.getPrice() - discount;
	    dbProduct.setDiscountPrice(discountPrice);

	    // Log calculations
	    System.out.println("Original Price: " + product.getPrice());
	    System.out.println("Discount: " + product.getDiscount());
	    System.out.println("Discount Amount: " + discount);
	    System.out.println("Discounted Price: " + discountPrice);

	    // Save updated product
	    Product updatedProduct = productRepository.save(dbProduct);

	    // Handle image upload if not empty
	    if (!ObjectUtils.isEmpty(updatedProduct) && !image.isEmpty()) {
	        try {
	            // Set the path to the folder where you want to save the image
	            File saveFile = new ClassPathResource("static/img/").getFile();
	            File productImgDir = new File(saveFile.getAbsolutePath() + File.separator + "product_img");

	            // Create directory if it does not exist
	            if (!productImgDir.exists()) {
	                Files.createDirectories(productImgDir.toPath());
	            }

	            // Save the image to the specified folder
	            Path path = Paths.get(productImgDir.getAbsolutePath() + File.separator + image.getOriginalFilename());
	            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	            System.out.println("Image uploaded to: " + path);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return updatedProduct != null ? product : null;
	}
*/

	
	
	@Override
	public List<Product> getAllActiveProducts(String category) {
		List<Product> products =null;
		if(ObjectUtils.isEmpty(category)) {
			products = productRepository.findByIsActiveTrue();
			System.out.println("---------productservice111="+products);		
		}else {
			products = productRepository.findByCategory(category);
			System.out.println("---------productservice=22222"+products);	
		}
		System.out.println("---------productservice=33"+products);
		return products;	
	}


	
	

}
