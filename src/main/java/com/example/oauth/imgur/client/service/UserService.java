package com.example.oauth.imgur.client.service;

import com.example.oauth.imgur.client.entity.AppUser;

public interface UserService {
	
	AppUser registerUser(AppUser userEntity);
	
	AppUser findByUsername(String username);

}
