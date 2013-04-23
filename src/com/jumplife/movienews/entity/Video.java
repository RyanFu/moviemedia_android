package com.jumplife.movienews.entity;

public class Video {
	private int id;
	private String name;
	private String videoUrl;
	private String posterUrl;
	
	public Video() {
		this(-1, "", "", "");
	}
	
	public Video(int id, String name, String videoUrl, String posterUrl) {
		this.id = id;
		this.name = name;
		this.videoUrl = videoUrl;
		this.posterUrl = posterUrl;
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
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public String getPosterUrl() {
		return posterUrl;
	}
	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}
}
