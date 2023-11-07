package com.example.oauth.imgur.client.exception;

public class UserAlreadyExistsException extends RuntimeException {
	 public UserAlreadyExistsException(String message){
	        super(message);
	    }
}
