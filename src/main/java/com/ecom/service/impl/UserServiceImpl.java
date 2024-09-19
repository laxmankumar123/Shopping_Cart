package com.ecom.service.impl;

import javax.lang.model.element.ModuleElement.UsesDirective;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserServic;


@Service
public class UserServiceImpl implements UserServic {
	
	@Autowired
	private UserRepository repository;

	
	@Override
	public UserDtls saveUser(UserDtls user) {
		UserDtls saveUser = repository.save(user);
		return saveUser;
	}

	
	
	
	
	
	
	
	
	
	
	
}
