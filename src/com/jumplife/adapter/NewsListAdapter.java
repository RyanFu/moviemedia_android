package com.jumplife.adapter;

import java.util.ArrayList;

import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;
import com.jumplife.movienews.entity.TextNews;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewsListAdapter extends BaseAdapter {
	private Activity mActivity;
	//private ArrayList<TextNews> newsContents;
	
	private ArrayList<News> news;
	
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	public NewsListAdapter(Activity mActivity,  ArrayList<News> news){
		this.news = news;
		this.mActivity = mActivity;
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
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
		
		LayoutInflater myInflater = LayoutInflater.from(mActivity);
		View converView = myInflater.inflate(R.layout.listview_news, null);
		
		RelativeLayout rlNews = (RelativeLayout)converView.findViewById(R.id.rl_new);
		
		ImageView imageviewNewsPhoto = (ImageView)converView.findViewById(R.id.news_poster);
		
		TextView textViewSource = (TextView)converView.findViewById(R.id.news_source);
		TextView textViewRealeaseDate = (TextView)converView.findViewById(R.id.news_date);
		TextView textvieTitle = (TextView)converView.findViewById(R.id.news_name);
		TextView textViewContent = (TextView)converView.findViewById(R.id.news_comment);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int Width = displayMetrics.widthPixels * 2 / 7;
        imageviewNewsPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageviewNewsPhoto.getLayoutParams().height = Width;
        imageviewNewsPhoto.getLayoutParams().width = Width;
		imageLoader.displayImage(news.get(position).getPosterUrl(), imageviewNewsPhoto, options);
		
		textvieTitle.setText(news.get(position).getName());
		textViewContent.setText(news.get(position).getComment());
		
		if(news.get(position).getOrigin() != null) 
			textViewSource.setText(news.get(position).getOrigin());
		
		if(news.get(position).getReleaseDate() != null)
			textViewRealeaseDate.setText(NewsAPI.dateToString(news.get(position).getReleaseDate()));
		
		rlNews.getLayoutParams().height = Width;
		
		return converView;

	}
}
