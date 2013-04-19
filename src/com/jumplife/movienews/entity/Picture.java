package com.jumplife.movienews.entity;

import java.util.Date;

public class Picture extends News{
	private String picUrl;
	
	public Picture(int id, String name, String posterUrl, NewsCategory category, String picUrl, Date releaseDate, String origin) {
		super(id, name, posterUrl, category, releaseDate, origin);
		this.picUrl = picUrl;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	@Override
	public String show() {
		return picUrl;
	}

	@Override
	public String getShareLink() {
		return picUrl;
	}
}
