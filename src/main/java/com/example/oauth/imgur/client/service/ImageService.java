package com.example.oauth.imgur.client.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
	
	public String uploadImage(MultipartFile file);

}
