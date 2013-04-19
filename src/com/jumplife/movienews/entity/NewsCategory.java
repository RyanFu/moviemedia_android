package com.jumplife.movienews.entity;

public class NewsCategory {
	private int id;
	private String name;
	private String posterUrl;
	private String iconUrl;
	private int typeId;
	

	public NewsCategory() {
		this(-1, "", "", "", -1);
	}
	
	public NewsCategory(int id, String name, String posterUrl, String iconUrl, int typeId) {
		this.id = id;
		this.name = name;
		this.posterUrl = posterUrl;
		this.iconUrl = iconUrl;
		this.typeId = typeId;
	}
	
	public NewsCategory(int id, String name, int typeId) {
		this(id, name, "", "", typeId);
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
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}	
}
