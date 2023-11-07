package com.example.oauth.imgur.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.oauth.imgur.client.entity.AppUser;
import com.example.oauth.imgur.client.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public AppUser registerUser(AppUser appUser) {
		log.info("saving user :" +appUser.getName());
		appUser.setPassword(bCryptPasswordEncoder.encode(appUser.getPassword()));
		return userRepository.save(appUser);
	}

	@Override
	public AppUser findByUsername(String username) {
		log.info("fetching user :" +username);
		return userRepository.findByUsername(username);
	}

}
