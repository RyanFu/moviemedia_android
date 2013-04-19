package com.jumplife.adapter;

import java.util.ArrayList;


import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsGridAdapter extends BaseAdapter {
	private Activity mActivity;
	
	//private ArrayList<NewsContent> newsContents;
	private ArrayList<News> news;
	
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	public NewsGridAdapter(Activity mActivity,  ArrayList<News> news){
		this.news = news;
		this.mActivity = mActivity;
	}

	public int getCount() {
		
		return news.size();
	}

	public Object getItem(int position) {

		return news.get(position);
	}

	public long getItemId(int position) {
	
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer
				((int)mActivity.getResources().getDimensionPixelSize(R.dimen.news_lv_item_radius)))
		.build();
		
		LayoutInflater myInflater = LayoutInflater.from(mActivity);
		View converView = myInflater.inflate(R.layout.listview_news, null);
		
		ImageView imageviewNewsPhoto = (ImageView)converView.findViewById(R.id.news_poster);
		
		TextView textViewSource = (TextView)converView.findViewById(R.id.news_source);
		TextView textViewRealeaseDate = (TextView)converView.findViewById(R.id.news_date);
		TextView textvieTitle = (TextView)converView.findViewById(R.id.news_name);
		TextView textViewContent = (TextView)converView.findViewById(R.id.news_comment);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels * 3 / 8;
        imageviewNewsPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageviewNewsPhoto.getLayoutParams().height = screenWidth * 3 / 5;
        imageviewNewsPhoto.getLayoutParams().width = screenWidth;
		imageLoader.displayImage(news.get(position).getPosterUrl(), imageviewNewsPhoto, options);
		
		textvieTitle.setText(news.get(position).getName());
		textViewContent.setText(news.get(position).getComment());
		
		if(news.get(position).getOrigin() != null) 
			textViewSource.setText(news.get(position).getOrigin());
		
		if(news.get(position).getReleaseDate() != null) 
			textViewRealeaseDate.setText(NewsAPI.dateToString(news.get(position).getReleaseDate()));
		
		return converView;

	}
}
