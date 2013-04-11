package com.jumplife.movienews.entity;

public class NewsCategories {
	private int id;
	private String name;
	private String posterUrl;
	private int typeId;
	
	public NewsCategories() {
		this(-1, "", "", -1);
	}
	
	public NewsCategories(int id, String name, String posterUrl, int typeId) {
		this.id = id;
		this.name = name;
		this.posterUrl = posterUrl;
		this.typeId = typeId;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPosterUrl() {
		return posterUrl;
	}
	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
}
