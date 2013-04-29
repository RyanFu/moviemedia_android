package com.jumplife.adapter;

import java.util.ArrayList;

import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.movienews.R;
import com.jumplife.movienews.entity.Video;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.viewpagerindicator.IconPagerAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class VideosViewPagerAdapter extends PagerAdapter implements IconPagerAdapter{

	private Activity mActivty;
	private ArrayList<Video> videos;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	public VideosViewPagerAdapter(Activity activty, ArrayList<Video> videos) {
		this.mActivty = activty;
		this.videos = videos;
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_loading)
		.showImageOnFail(R.drawable.img_status_nopicture)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}
	
	@Override
	public int getCount() {
		return videos.size();
	}
	
	@Override
	public void destroyItem(View pager, int position, Object view) {
		((ViewPager) pager).removeView(((ViewPager)pager).getChildAt(position));
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}	
	
	@SuppressWarnings("deprecation")
	@Override
	public Object instantiateItem(View pager, int pos) {
        
		View view = View.inflate(mActivty, R.layout.video_viewpage_item, null);
        TextView textViewContext = (TextView)view.findViewById(R.id.pager_context);        
        ImageView imageViewMoviePoster = (ImageView)view.findViewById(R.id.pager_poster);        
        ImageView imageViewMoviePlay = (ImageView)view.findViewById(R.id.pager_play);
        
        DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivty.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        imageViewMoviePoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams ivrlParams = new RelativeLayout.LayoutParams
				(screenWidth, (int)(screenWidth / 2));

		ivrlParams.setMargins(mActivty.getResources().getDimensionPixelSize(R.dimen.news_content_video_poster_board), 
				0,
				mActivty.getResources().getDimensionPixelSize(R.dimen.news_content_video_poster_board), 
				0);
		imageViewMoviePoster.setLayoutParams(ivrlParams);
		
		RelativeLayout.LayoutParams ivrlPlayParams = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		ivrlPlayParams.addRule(RelativeLayout.ALIGN_BOTTOM, imageViewMoviePoster.getId());
		ivrlPlayParams.addRule(RelativeLayout.ALIGN_TOP, imageViewMoviePoster.getId());
		ivrlPlayParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		ivrlPlayParams.setMargins(mActivty.getResources().getDimensionPixelSize(R.dimen.news_content_video_play_board), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.news_content_video_play_board), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.news_content_video_play_board), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.news_content_video_play_board));
		imageViewMoviePlay.setLayoutParams(ivrlPlayParams);
        
        textViewContext.setText(videos.get(pos).getName());
        textViewContext.setGravity(Gravity.CENTER);
		textViewContext.setTextColor(mActivty.getResources().getColor(R.color.video_description));
		textViewContext.setBackgroundResource(mActivty.getResources().getColor(R.color.transparent100));
		RelativeLayout.LayoutParams tvrlParams = new RelativeLayout.LayoutParams
				(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        tvrlParams.addRule(RelativeLayout.BELOW, imageViewMoviePoster.getId());
		tvrlParams.setMargins(mActivty.getResources().getDimensionPixelSize(R.dimen.news_content_video_board), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.news_content_video_board), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.news_content_video_board), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.news_content_video_board));
		textViewContext.setLayoutParams(tvrlParams);
		
		imageLoader.displayImage(videos.get(pos).getPosterUrl(), imageViewMoviePoster, options);
        
		view.setOnClickListener(new ItemButtonClick(pos));
		view.setOnTouchListener(new OnTouchListener() {

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
		
        ((ViewPager)pager).addView(view, ((ViewPager)pager).getChildCount() > pos ? pos : ((ViewPager)pager).getChildCount());
        
	    return view;
	}

	class ItemButtonClick implements OnClickListener {
		private int position;

		ItemButtonClick(int pos) {
			position = pos;
		}

		public void onClick(View v) {
			EasyTracker.getTracker().sendEvent("影片", "點擊", "video id: " + videos.get(position).getId(),
				(long)(videos.get(position).getId()));
			Uri uri = Uri.parse(videos.get(position).getVideoUrl());
    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
    		mActivty.startActivity(it);
		}
	}
	
	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return index;
	}
}