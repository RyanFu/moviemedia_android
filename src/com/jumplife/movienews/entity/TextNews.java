package com.jumplife.movienews.entity;

import java.util.ArrayList;
import java.util.Date;

public class TextNews extends News{
	
	private String content;
	private String sourceUrl;
	private ArrayList<Video> videoList; 
	
	public TextNews(int id, String name, String posterUrl, NewsCategory category, String comment, Date releaseDate, String content, String sourceUrl, String origin) {
		this(id, name, posterUrl, category, comment, releaseDate, content, sourceUrl, new ArrayList<Video>(0), origin);
	}
	
	public TextNews(int id, String name, String posterUrl, NewsCategory category, String comment, Date releaseDate, String content, String sourceUrl, ArrayList<Video> videoList, String origin) {
		super(id, name, posterUrl, category, comment, releaseDate, origin);
		this.content = content;
		this.sourceUrl = sourceUrl;
		this.videoList = videoList;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	@Override
	public String show() {
		return content;
	}

	@Override
	public String getShareLink() {
		return sourceUrl;
	}

	public ArrayList<Video> getVideoList() {
		return videoList;
	}

	public void setVideoList(ArrayList<Video> videoList) {
		this.videoList = videoList;
	}
	
}
