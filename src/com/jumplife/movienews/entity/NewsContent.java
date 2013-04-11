package com.jumplife.movienews.entity;

import java.util.ArrayList;

public class NewsContent {
	private int id;
	private int typeId;
	private String name;
	private String comment;
	private String content;
	private String posterUrl;
	private String source;
	private String releaseDate;
	private ArrayList<Video> videos;
	
	public NewsContent() {
		this(-1, -1, "", "", "", "", "", "", new ArrayList<Video>());
	}
	
	public NewsContent(int id, int typeId, String name, String comment, String content, 
			String posterUrl, String source, String releaseDate, ArrayList<Video> videos) {
		this.id = id;
		this.typeId = typeId;
		this.name = name;
		this.comment = comment;
		this.content = content;
		this.posterUrl = posterUrl;
		this.source = source;
		this.releaseDate = releaseDate;
		this.videos = videos;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPosterUrl() {
		return posterUrl;
	}
	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public ArrayList<Video> getVideos() {
		return videos;
	}
	public void setVideos(ArrayList<Video> videos) {
		this.videos = videos;
	}
}
