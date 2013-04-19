package com.jumplife.movienews.entity;

import java.util.Date;

public abstract class News {
	protected int id; //id
	protected String name; //新聞標題
	protected String posterUrl; //新聞縮圖
	protected String origin; //新聞來源
	
	protected NewsCategory category; //新聞類別
	
	protected String comment; //小編的話
	protected Date releaseDate; //上架日期
	
	public News (int id, String name, String posterUrl, NewsCategory category, String comment, Date releaseDate, String origin) {
		this.id = id;
		this.name = name;
		this.posterUrl = posterUrl;
		this.category = category;
		this.comment = comment;
		this.origin = origin;
		this.releaseDate = releaseDate;
	}
	
	public News (int id, String name, String posterUrl, NewsCategory category, Date releaseDate, String origin) {
		this(id, name, posterUrl, category, "", releaseDate, origin);
	}
	
	public News (int id, String name, String posterUrl, NewsCategory category) {
		this(id, name, posterUrl, category, "", new Date(), "");
	}
	
	public abstract String show(); //顯示內容
	public abstract String getShareLink();
	
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
	public NewsCategory getCategory() {
		return this.category;
	}
	public void setCategory(NewsCategory category) {
		this.category = category;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
}
