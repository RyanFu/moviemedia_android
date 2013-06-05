package com.jumplife.movienews.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.movienews.entity.*;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class NewsAPI {
	
	private String urlAddress;
	private HttpURLConnection connection;
	private String requestedMethod;
	private Activity mActivity;
	private int connectionTimeout;
	private int readTimeout;
	private boolean usercaches;
	private boolean doInput;
	private boolean doOutput;
	
	public static final String TAG = "NEWS_API";
	public static final boolean DEBUG = false;
	
	public NewsAPI(String urlAddress, int connectionTimeout, int readTimeout) {
		this.urlAddress = new String(urlAddress + "/");
		this.connectionTimeout = connectionTimeout;
		this.readTimeout = readTimeout;
		this.usercaches = false;
		this.doInput = true;
		this.doOutput = true;
	}
	public NewsAPI(String urlAddress) {
		this(new String(urlAddress), 5000, 5000);
	}
	
	public NewsAPI(Activity a) {
		this(new String("http://mmedia.jumplife.com.tw"));
		this.mActivity = a;
	}
	
	public NewsAPI() {
		this(new String("http://mmedia.jumplife.com.tw"));
	}
	
	public int connect(String requestedMethod, String apiPath) {
		int status = -1;
		try {
			URL url = new URL(urlAddress + apiPath);
			
			if(DEBUG)
				Log.d(TAG, "URL: " + url.toString());
			connection = (HttpURLConnection) url.openConnection();
					
			connection.setRequestMethod(requestedMethod);
			connection.setReadTimeout(this.readTimeout);
			connection.setConnectTimeout(this.connectionTimeout);
			connection.setUseCaches(this.usercaches);
			connection.setDoInput(this.doInput);
			connection.setDoOutput(this.doOutput);
			connection.setRequestProperty("Content-Type",  "application/json;charset=utf-8");
			
			connection.connect();

		} 
		catch (MalformedURLException e1) {
			e1.printStackTrace();
			return status;
		}
		catch (IOException e) {
			e.printStackTrace();
			return status;
		}
		
		return status;
	}
	
	public void disconnect()
	{
		connection.disconnect();
	}
	
	//取得電影類別
	public ArrayList<NewsCategory> getCategoryList() {
		ArrayList<NewsCategory> categoryList = new ArrayList<NewsCategory>(10);
		String message = getMessageFromServer("GET", "api/v1/categories.json", null);
		
		if(message == null) {
			return null;
		}
		else {
			JSONArray catogorysArray;
			try {
				catogorysArray = new JSONArray(message.toString());
				for (int i = 0; i < catogorysArray.length() ; i++) {
					JSONObject categoryJson = catogorysArray.getJSONObject(i);
					int id = categoryJson.getInt("id");
					String name = categoryJson.getString("name");
					String posterUrl = categoryJson.getString("poster_url");
					int typeId = categoryJson.getInt("type_id");
					
					//NewsCategory(int id, String name, String posterUrl, String iconUrl, int typeId)
					categoryList.add(new NewsCategory(id, name, posterUrl, "", typeId));
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return categoryList;
	}
	
	
	public ArrayList<News> getEditorSelectedList () {
		ArrayList<News> newsList = new ArrayList<News>(10);
		String message = getMessageFromServer("GET", "api/v1/news/editor_selected", null);
		
		if(message == null) {
			return null;
		}
		else {
			JSONArray newsArray;
			
			try {
				newsArray = new JSONArray(message.toString());
				for (int i = 0; i < newsArray.length() ; i++) {
					JSONObject newsJson = newsArray.getJSONObject(i).getJSONObject("news");
					int id = newsJson.getInt("id");
					String name = newsJson.getString("title"); 
					String posterUrl = newsJson.getString("smallposter_url");
					String origin = newsJson.getString("origin");
					int typeId = newsJson.getInt("type_id");
					int categoryId = newsJson.getInt("category_id");
					String categoryName = newsJson.getString("category_name");
					
					NewsCategory category = new NewsCategory(categoryId, categoryName, typeId);
					
					//(int id, String name, int typeId)
					newsList.add(new Picture(id, name, posterUrl, category, posterUrl, new Date(), origin));
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return newsList;
	}
	
	public ArrayList<News> getNewsList(int categoryId, int typeId ,int pageIndex) {
		ArrayList<News> newsList = new ArrayList<News>(10);
		//http://106.187.53.220/api/v1/news.json?category_id=1&page=1
		String message = getMessageFromServer("GET", "api/v1/news.json?category_id=" + categoryId + "&page=" + pageIndex, null);
		
		NewsCategory category = new NewsCategory(categoryId, "", "", "", typeId);
		
		if(message == null) {
			return null;
		}
		else {
			JSONArray newsArray;
			try {
				newsArray = new JSONArray(message.toString());
				for (int i = 0; i < newsArray.length() ; i++) {
					//Log.d(TAG, )
					JSONObject newsJson = null;
					if(typeId == 1) {
						newsJson = newsArray.getJSONObject(i);//.getJSONObject("news");
					}
					else if(typeId == 2) {
						newsJson = newsArray.getJSONObject(i).getJSONObject("news");
					}
					int id = newsJson.getInt("id");
					String name = newsJson.getString("title");
					String origin = newsJson.getString("origin");
					Date releaseDate = NewsAPI.stringToDate(newsJson.getString("release_date"));

					String posterUrl = "";
					String picUrl = "";
					String comment = "";
					
					if(typeId == 1) {
						comment =  newsJson.getString("comment");
						posterUrl = newsJson.getString("smallposter_url");
						//文字新聞 attr: id, comment, origin, release_date, smallposter_url, title

						TextNews text = new TextNews(id, name, posterUrl, category, comment, releaseDate, "", "", origin);
						newsList.add(text);
					}
					else if(typeId == 2) {
						posterUrl = newsJson.getString("poster_url");
						picUrl = newsJson.getString("poster_url");
						//圖片新聞 attr: id, poster_url, origin, title, release_date
						Picture pic = new Picture(id, name, posterUrl, category, picUrl, releaseDate, origin);
						newsList.add(pic);
					}
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
	
		return newsList;
	}

	public TextNews getTextNews(int newsId) {
		//http://106.187.53.220/api/v1/news/1.json
		String message = getMessageFromServer("GET", "api/v1/news/" + newsId + ".json", null);
		
		TextNews news = null;
		
		//newsId, content, contentUrl     video -> name, pic_url, video_url
		if(message == null) {
			return null;
		}
		else {
			try {
				JSONObject json = new JSONObject(message);
				JSONObject newsJson = json.getJSONObject("news");
				
				String content = newsJson.getString("content");
				String contentUrl = newsJson.getString("content_url");
				
				JSONArray videoAry = json.getJSONArray("videos");
				
				if (videoAry.length() > 0) {
					ArrayList<Video> videoList = new ArrayList<Video>(5);
					
					for (int i =  0; i < videoAry.length(); i++) {
						int id = videoAry.getJSONObject(i).getInt("id");
						String name = videoAry.getJSONObject(i).getString("name");
						String videoUrl = videoAry.getJSONObject(i).getString("video_url");
						String posterUrl = videoAry.getJSONObject(i).getString("pic_url");
						videoList.add(new Video(id, name, videoUrl, posterUrl));
					}
					
					news = new TextNews(-1, "", "", null, "", new Date(), content, contentUrl, videoList, "");
				}
				else {
					news = new TextNews(-1, "", "", null, "", new Date(), content, contentUrl, "");
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return news;
	}
	
	public ArrayList<AppProject> getAppProjectList (Activity mActivity) {
		ArrayList<AppProject> appList = new ArrayList<AppProject>(10);
		String message = getMessageFromServer("GET", "api/v1/appprojects.json", null);
		
		if(message == null) {
			return null;
		}
		else {
			JSONArray appArray;
			
			try {
				appArray = new JSONArray(message.toString());
				for (int i = 0; i < appArray.length() ; i++) {
					JSONObject appJson = appArray.getJSONObject(i);
					String name = appJson.getString("name"); 
					String iconurl = appJson.getString("iconurl");
					String pack = appJson.getString("pack");
					String clas = appJson.getString("clas");
					
					if(!mActivity.getApplicationContext().getPackageName().equals(pack)) {
				    	AppProject appProject = new AppProject(name, iconurl, pack, clas);
				    	appList.add(appProject);
				    }
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return appList;
	}
	
	public boolean updateNewsShares(int newsId) {
		boolean result = false;

		try{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			String url = "http://mmedia.jumplife.com.tw/api/v1/news/" + newsId + ".json";						
			if(DEBUG)
				Log.d(TAG, "URL : " + url);
			
			HttpPut httpPut = new HttpPut(url);
			HttpResponse response = httpClient.execute(httpPut);
			
			StatusLine statusLine =  response.getStatusLine();
			if (statusLine.getStatusCode() == 200){
				result = true;
			}
		} 
	    catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;
		} 
		catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;
		} 
		catch (IOException e){
			e.printStackTrace();
			return result;
		}	
		return result;
	} 
	
	public boolean updateNewsWatchedWithAccount(int newsId) {
		boolean result = false;
		String account = "";
		String utmSource = "";
		/*
		Intent intent = mActivity.getIntent();
	    Uri uri = intent.getData();
	    
	    if (uri != null) {
	    	if((uri.getQueryParameter("utm_source") != null) && (uri.getQueryParameter("utm_source").length() > 0)) {    // Use campaign parameters if avaialble.
	    		utmSource = uri.getPath();
	        }
	    	else {
	    		utmSource = "native";
	    	}
	    }
	    else {
	    	utmSource = "native";
	    }
		*/		
		SharePreferenceIO sharePreferenceIO = new SharePreferenceIO(mActivity);
		utmSource = sharePreferenceIO.SharePreferenceO("utm_source", "native");
		
		AccountManager accountManager = AccountManager.get(mActivity);
		Account[] accounts = accountManager.getAccountsByType("com.google");
		if(accounts.length > 0) {
			account = accounts[0].name;
	
			try{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String url = "http://mmedia.jumplife.com.tw/api/v1/news/update_device_watch.json?" +
						"user_email=" + account +
						"&news_id=" + newsId +
						"&utm_source=" + utmSource;						
				if(DEBUG)
					Log.d(TAG, "URL : " + url);
				
				HttpPut httpPut = new HttpPut(url);
				HttpResponse response = httpClient.execute(httpPut);
				
				StatusLine statusLine =  response.getStatusLine();
				if (statusLine.getStatusCode() == 200){
					result = true;
				}
			} 
		    catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return result;
			} 
			catch (ClientProtocolException e) {
				e.printStackTrace();
				return result;
			} 
			catch (IOException e){
				e.printStackTrace();
				return result;
			}
		}
		return result;
	}
	
	public String getMessageFromServer(String requestMethod, String apiPath, JSONObject json) {
		URL url;
		try {
			url = new URL(this.urlAddress +  apiPath);
			if(DEBUG)
				Log.d(TAG, "URL: " + url);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(requestMethod);
			
			connection.setRequestProperty("Content-Type",  "application/json;charset=utf-8");
			if(requestMethod.equalsIgnoreCase("POST"))
				connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.connect();
			
			
			if(requestMethod.equalsIgnoreCase("POST")) {
				OutputStream outputStream;
				
				outputStream = connection.getOutputStream();
				if(DEBUG)
					Log.d("post message", json.toString());
				
				outputStream.write(json.toString().getBytes());
				outputStream.flush();
				outputStream.close();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder lines = new StringBuilder();;
			String tempStr;
			
			while ((tempStr = reader.readLine()) != null) {
	            lines = lines.append(tempStr);
	        }
			if(DEBUG)
				Log.d(TAG, lines.toString());
			
			reader.close();
			connection.disconnect();
			
			return lines.toString();
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String[] getPromotion() {
				
		String message = getMessageFromServer("GET", "api/promotion.json", null);
		String[] tmp = new String[5];
				
		if(message == null) {
			return null;
		}
		try{
			JSONObject responseJson = new JSONObject(message);
			
			tmp[0] = (responseJson.getString("picture_link"));
			tmp[1] = (responseJson.getString("link"));
			tmp[2] = (responseJson.getString("tilte"));
			tmp[3] = (responseJson.getString("description"));
			tmp[4] = (responseJson.getString("version"));
		} 
		catch (JSONException e){
			e.printStackTrace();
			return null;
		}
		
		return tmp;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String dateToString(Date date) {
		DateFormat releaseFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = releaseFormatter.format(date);
		return dateStr;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static Date stringToDate(String dateStr) {
		DateFormat releaseFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date= null;
		try {
			date = releaseFormatter.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return date;
	}
	
	
	public String getUrlAddress() {
		return urlAddress;
	}
	public void setUrlAddress(String urlAddress) {
		this.urlAddress = urlAddress;
	}
	public HttpURLConnection getConnection() {
		return connection;
	}
	public void setConnection(HttpURLConnection connection) {
		this.connection = connection;
	}
	public String getRequestedMethod() {
		return requestedMethod;
	}
	public void setRequestedMethod(String requestedMethod) {
		this.requestedMethod = requestedMethod;
	}
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public boolean isUsercaches() {
		return usercaches;
	}
	public void setUsercaches(boolean usercaches) {
		this.usercaches = usercaches;
	}
	public boolean isDoInput() {
		return doInput;
	}
	public void setDoInput(boolean doInput) {
		this.doInput = doInput;
	}
	public boolean isDoOutput() {
		return doOutput;
	}
	public void setDoOutput(boolean doOutput) {
		this.doOutput = doOutput;
	}
}
