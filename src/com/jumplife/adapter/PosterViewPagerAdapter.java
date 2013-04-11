package com.jumplife.adapter;

import java.util.ArrayList;

import com.jumplife.movienews.NewsContentPhoneActivity;
import com.jumplife.movienews.PicturesPhoneActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.entity.Picture;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.viewpagerindicator.IconPagerAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class PosterViewPagerAdapter extends PagerAdapter implements IconPagerAdapter{

	private Activity mActivty;
	private ArrayList<Picture> pictures;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	public PosterViewPagerAdapter(Activity activty, ArrayList<Picture> pictures) {
		this.mActivty = activty;
		this.pictures = pictures;
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}
	
	@Override
	public int getCount() {
		return pictures.size();
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
        
		View view = View.inflate(mActivty, R.layout.poster_viewpage_item, null);
        TextView textViewContext = (TextView)view.findViewById(R.id.pager_context);
        TextView textViewFeature = (TextView)view.findViewById(R.id.pager_type);        
        ImageView imageViewMoviePoster = (ImageView)view.findViewById(R.id.pager_poster);
        
        DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivty.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        imageViewMoviePoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageViewMoviePoster.getLayoutParams().height = (int)(screenWidth / 2);
        imageViewMoviePoster.getLayoutParams().width = screenWidth;
        
        textViewFeature.setText(pictures.get(pos).getSource());
        RelativeLayout.LayoutParams rlFeatureParams = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rlFeatureParams.setMargins(mActivty.getResources().getDimensionPixelSize(R.dimen.feature_margin), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.feature_margin), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.feature_margin), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.feature_margin));
        rlFeatureParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rlFeatureParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		textViewFeature.setGravity(Gravity.CENTER);
		textViewFeature.setPadding(mActivty.getResources().getDimensionPixelSize(R.dimen.feature_padding), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.feature_padding), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.feature_padding), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.feature_padding));
		textViewFeature.setLayoutParams(rlFeatureParams);
		if(pictures.get(pos).getSource() == null || pictures.get(pos).getSource().contains(""))
			textViewFeature.setVisibility(View.INVISIBLE);
		
        textViewContext.setText(pictures.get(pos).getContent());
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
				(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		textViewContext.setGravity(Gravity.CENTER);
		textViewContext.setPadding(mActivty.getResources().getDimensionPixelSize(R.dimen.text_board), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.text_board), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.text_board), 
				mActivty.getResources().getDimensionPixelSize(R.dimen.text_board));
		textViewContext.setLayoutParams(rlParams);
		
		imageLoader.displayImage(pictures.get(pos).getPicUrl(), imageViewMoviePoster, options);
        
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
			Intent newAct = new Intent();
			Bundle bundle = new Bundle();
            if (pictures.get(position).getTypeId() == 1) {
				newAct.setClass(mActivty, NewsContentPhoneActivity.class );
				bundle.putInt("featureId", pictures.get(position).getId());
	            bundle.putString("featureName", pictures.get(position).getSource());
	            newAct.putExtras(bundle);
			} else {
				newAct.setClass(mActivty, PicturesPhoneActivity.class );
				bundle.putInt("featureId", pictures.get(position).getId());
	            bundle.putString("featureName", pictures.get(position).getSource());
	            newAct.putExtras(bundle);
			}            
            mActivty.startActivity(newAct);
		}
	}

	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return index;
	}
}