package com.jumplife.movienews.asynctask;

import android.os.AsyncTask;

import com.jumplife.movienews.api.NewsAPI;

public class NewsShareTask extends AsyncTask<Integer, Integer, String>{
	
	private int newsId;
	
	public NewsShareTask (int newsId) {
		this.newsId = newsId;
	}

	@Override  
    protected void onPreExecute() {
		super.onPreExecute();
    }  
      
    @Override  
    protected String doInBackground(Integer... params) {
    	NewsAPI api = new NewsAPI();
    	api.updateNewsShares(newsId);
        return "";  
    }  

    @Override  
    protected void onProgressUpdate(Integer... progress) {    
        super.onProgressUpdate(progress);  
    }  

    @Override  
    protected void onPostExecute(String result) {
        super.onPostExecute(result);  
    }
}