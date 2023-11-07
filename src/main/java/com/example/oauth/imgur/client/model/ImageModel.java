package com.example.oauth.imgur.client.model;

public class ImageModel {
	
	private String name;
	private String Type;
	private byte[] imageData;
	public String getName() {
		return name;
	}
	
	public ImageModel(String name, String type, byte[] imageData) {
		super();
		this.name = name;
		Type = type;
		this.imageData = imageData;
	}
	
	public ImageModel() {
		super();
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public byte[] getImageData() {
		return imageData;
	}
	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}
	
	

}
