package com.jumplife.adapter;

import java.util.ArrayList;

import com.jumplife.movienews.R;
import com.jumplife.movienews.entity.Video;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class VideoListAdapter extends BaseAdapter {
	private Activity mActivity;
	private ArrayList<Video> videos;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	public VideoListAdapter(Activity mActivity,  ArrayList<Video> videos){
		this.videos = videos;
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
		
		return videos.size();
	}

	public Object getItem(int position) {

		return videos.get(position);
	}

	public long getItemId(int position) {
	
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater myInflater = LayoutInflater.from(mActivity);
		View converView = myInflater.inflate(R.layout.video_viewpage_item, null);
		
		TextView textViewContext = (TextView)converView.findViewById(R.id.pager_context);        
        ImageView imageViewMoviePoster = (ImageView)converView.findViewById(R.id.pager_poster);        
        ImageView imageViewMoviePlay = (ImageView)converView.findViewById(R.id.pager_play);
		
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels * 3 / 10;
        imageViewMoviePoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams ivrlParams = new RelativeLayout.LayoutParams
				(screenWidth, (int)(screenWidth / 2));

		ivrlParams.setMargins(mActivity.getResources().getDimensionPixelSize(R.dimen.news_content_video_poster_board), 
				0,
				mActivity.getResources().getDimensionPixelSize(R.dimen.news_content_video_poster_board), 
				0);
		imageViewMoviePoster.setLayoutParams(ivrlParams);
		imageViewMoviePoster.setOnClickListener(new ItemButtonClick(position));
		imageViewMoviePoster.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent me) {
				ImageView imageViewMoviePoster = (ImageView)v.findViewById(R.id.pager_poster);
				
				if (me.getAction() == MotionEvent.ACTION_DOWN) {
					imageViewMoviePoster.setColorFilter(Color.argb(150, 0, 0, 0));
	            } else if (me.getAction() == MotionEvent.ACTION_UP) {
	            	imageViewMoviePoster.setColorFilter(Color.argb(0, 0, 0, 0)); 
	            } else if (me.getAction() == MotionEvent.ACTION_CANCEL) {
	            	imageViewMoviePoster.setColorFilter(Color.argb(0, 0, 0, 0)); 
	            }
	            return false;
	        }

	    });		
		imageLoader.displayImage(videos.get(position).getPosterUrl(), imageViewMoviePoster, options);		
		
		RelativeLayout.LayoutParams ivrlPlayParams = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ivrlPlayParams.addRule(RelativeLayout.ALIGN_BOTTOM, imageViewMoviePoster.getId());
		ivrlPlayParams.addRule(RelativeLayout.ALIGN_TOP, imageViewMoviePoster.getId());
		ivrlPlayParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		ivrlPlayParams.setMargins(mActivity.getResources().getDimensionPixelSize(R.dimen.news_content_video_play_board), 
				mActivity.getResources().getDimensionPixelSize(R.dimen.news_content_video_play_board), 
				mActivity.getResources().getDimensionPixelSize(R.dimen.news_content_video_play_board), 
				mActivity.getResources().getDimensionPixelSize(R.dimen.news_content_video_play_board));
		imageViewMoviePlay.setLayoutParams(ivrlPlayParams);
        
        textViewContext.setText(videos.get(position).getName());
        textViewContext.setGravity(Gravity.CENTER);
		textViewContext.setTextColor(mActivity.getResources().getColor(R.color.main_color_blue));
		textViewContext.setBackgroundResource(mActivity.getResources().getColor(R.color.transparent100));
		RelativeLayout.LayoutParams tvrlParams = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvrlParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tvrlParams.addRule(RelativeLayout.BELOW, imageViewMoviePoster.getId());
		tvrlParams.setMargins(mActivity.getResources().getDimensionPixelSize(R.dimen.news_content_video_board), 
				mActivity.getResources().getDimensionPixelSize(R.dimen.news_content_video_board), 
				mActivity.getResources().getDimensionPixelSize(R.dimen.news_content_video_board), 
				mActivity.getResources().getDimensionPixelSize(R.dimen.news_content_video_board));
		textViewContext.setLayoutParams(tvrlParams);
		
		return converView;
	}
	
	class ItemButtonClick implements OnClickListener {
		private int position;

		ItemButtonClick(int pos) {
			position = pos;
		}

		public void onClick(View v) {
			Uri uri = Uri.parse(videos.get(position).getVideoUrl());
    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
    		mActivity.startActivity(it);
		}
	}
}
