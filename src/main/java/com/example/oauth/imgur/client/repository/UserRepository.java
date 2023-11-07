package com.example.oauth.imgur.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.oauth.imgur.client.entity.AppUser;
import com.example.oauth.imgur.client.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
	
	public AppUser findByUsername(String username);

}
