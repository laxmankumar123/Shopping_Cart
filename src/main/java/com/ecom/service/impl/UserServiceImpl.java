package com.ecom.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;


import javax.lang.model.element.ModuleElement.UsesDirective;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserServic;
import com.ecom.util.AppConstant;


@Service
public class UserServiceImpl implements UserServic {
	
	@Autowired
	private UserRepository userRepository;

	
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserDtls saveUser(UserDtls user) {
		
		user.setRole("ROLE_USER");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		
		
		
		String encodePassword = passwordEncoder.encode(user.getPassword());
		
		user.setPassword(encodePassword);
		
		UserDtls saveUser = userRepository.save(user);
		return saveUser;
	}

	@Override
	public UserDtls getUserByEmail(String email) {
		// TODO Auto-generated method stub
		
		
		
		return userRepository.findByEmail(email);
	}

	@Override
	public List<UserDtls> getUsers(String role) {
		
		return userRepository.findByRole(role);
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		
		Optional<UserDtls> findByuser = userRepository.findById(id);
		
		if(findByuser.isPresent()) {
			UserDtls userDtls = findByuser.get();
			userDtls.setIsEnable(status);
			userRepository.save(userDtls);
			return true;
		}
		return false;
		// TODO Auto-generated method stub
		
	}
	
	

	@Override
	public void increaseFailedAttempt(UserDtls user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		userRepository.save(user);
	}


	@Override
	public void userAccountLock(UserDtls user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new java.util.Date());
		userRepository.save(user);
	}


	@Override
	public boolean unlockAccountTimeExpired(UserDtls user) {
		long lockTime = user.getLockTime().getTime();
		long unLockTime=lockTime+AppConstant.UNLOCK_DURATION_TIME;
		
		long currentTime = System.currentTimeMillis();
		
		if(unLockTime<currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepository.save(user);
			return true;	
		}
		
	
		return false;
	}

	@Override
	public void resetAttempt(int userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserDtls findEmail = userRepository.findByEmail(email);
		findEmail.setReset_token(resetToken);
		System.out.println("=======resetToken"+resetToken);
		userRepository.save(findEmail);
	}

	
	
	
	
	
	
	
	
	
	
	
}
