package com.ecom.config;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

import org.apache.catalina.startup.UserDatabase;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecom.model.UserDtls;

public class CustomUser implements UserDetails{
	
	public CustomUser(UserDtls user) {
		super();
		this.user = user;
	}

	private UserDtls user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority= new SimpleGrantedAuthority(user.getRole());
		return Arrays.asList(authority);
	}

	@Override
	public String getPassword() {
		
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		
		return user.getEmail();
	}
	
	@Override
	public boolean isEnabled() {
		
		
		return user.getIsEnable();
	}

}
