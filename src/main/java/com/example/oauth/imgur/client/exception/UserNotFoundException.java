package com.example.oauth.imgur.client.exception;

public class UserNotFoundException extends RuntimeException {
	 public UserNotFoundException(String message){
	        super(message);
	    }
}
