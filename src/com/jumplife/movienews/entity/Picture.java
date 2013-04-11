package com.jumplife.movienews.entity;

public class Picture {
	private int id;
	private int typeId;
	private String content;
	private String picUrl;
	private String source;
	
	public Picture() {
		this(-1, -1, "", "", "");
	}
	
	public Picture(int id, int typeId, String content, String picUrl, String source) {
		this.id = id;
		this.typeId = typeId;
		this.content = content;
		this.picUrl = picUrl;
		this.source = source;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
}
