package com.ecom.service.impl;

import javax.lang.model.element.ModuleElement.UsesDirective;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserServic;


@Service
public class UserServiceImpl implements UserServic {
	
	@Autowired
	private UserRepository repository;

	
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserDtls saveUser(UserDtls user) {
		
		user.setRole("ROLE_USER");
		String encodePassword = passwordEncoder.encode(user.getPassword());
		
		user.setPassword(encodePassword);
		
		UserDtls saveUser = repository.save(user);
		return saveUser;
	}

	
	
	
	
	
	
	
	
	
	
	
}
