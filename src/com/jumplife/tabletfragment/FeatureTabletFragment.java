package com.jumplife.tabletfragment;

import java.util.ArrayList;

import com.jumplife.movienews.NewsContentTabletActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.entity.Picture;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class FeatureTabletFragment extends Fragment {	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private LinearLayout llFeature;
	private ArrayList<Picture> pictures;
	private LoadPictureTask loadPictureTask;
	private ProgressBar pbInit;
	
	private FragmentActivity mFragmentActivity;

    @Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_feature, container, false);		
		initView();
		
		loadPictureTask = new LoadPictureTask();
	    if(Build.VERSION.SDK_INT < 11)
	    	loadPictureTask.execute();
        else
        	loadPictureTask.executeOnExecutor(LoadPictureTask.THREAD_POOL_EXECUTOR, 0);
	    
		return fragmentView;
	}
	
	private void initView() {
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_feature);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		llFeature = (LinearLayout)fragmentView.findViewById(R.id.ll_feature);
		
		imageButtonRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadPictureTask = new LoadPictureTask();
        	    if(Build.VERSION.SDK_INT < 11)
        	    	loadPictureTask.execute();
                else
                	loadPictureTask.executeOnExecutor(LoadPictureTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
	}	
	
	private void fetchPictureData() {
		pictures = new ArrayList<Picture>();
		pictures = fakePictures();
	}
	
	private ArrayList<Picture> fakePictures() {
		Picture tmp1 = new Picture(11, 1, "手工彩繪Star wars 所有人物", "http://pic.pimg.tw/jumplives/1364382592-2675134844.jpg?v=1364382593", "");
		Picture tmp2 = new Picture(22, 2, "阿凡達幕後", "http://pic.pimg.tw/jumplives/1364382592-3648714962.jpg?v=1364382593", "");
		ArrayList<Picture> tmps = new ArrayList<Picture>();
		tmps.add(tmp1);
		tmps.add(tmp2);
		return tmps;
	}
	
	@SuppressWarnings("deprecation")
	private void setPictureView() {
		LayoutInflater myInflater = LayoutInflater.from(mFragmentActivity);
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		if(pictures.size() > 0) {
			//TableRow Schedule_row = new TableRow(getActivity());
			View converView = myInflater.inflate(R.layout.poster_viewpage_item, null);
			TextView tv = (TextView)converView.findViewById(R.id.pager_context);
			ImageView iv = (ImageView)converView.findViewById(R.id.pager_poster);
				
			DisplayMetrics displayMetrics = new DisplayMetrics();
			((Activity) mFragmentActivity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	        int screenWidth = displayMetrics.widthPixels * 3 / 4 - 
	        		mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width) * 2;
	        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
	        iv.getLayoutParams().height = (int)(screenWidth / 2);
	        iv.getLayoutParams().width = screenWidth;
		        
				
			tv.setText(pictures.get(0).getContent());
			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
					(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			tv.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board));
			tv.setLayoutParams(rlParams);
			imageLoader.displayImage(pictures.get(0).getPicUrl(), iv, options);
			converView.setId(0);
			if(pictures.get(0).getTypeId() == 1) {
				converView.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						Intent newAct = new Intent();
						int index = arg0.getId();
						newAct.setClass(mFragmentActivity, NewsContentTabletActivity.class );
					
			            Bundle bundle = new Bundle();
			            bundle.putInt("featureId", pictures.get(index).getId());
			            bundle.putString("featureName", pictures.get(index).getSource());
			            newAct.putExtras(bundle);
			            startActivity(newAct);
					}						
				});
			} else {
				converView.setClickable(false);
			}
			
			TableRow.LayoutParams Params = new TableRow.LayoutParams
					(screenWidth, screenWidth / 2, 1.0f);
			Params.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
					0);

			converView.setBackgroundResource(R.drawable.item_background);
			converView.setLayoutParams(Params);
			llFeature.addView(converView);
		}
			
		for(int i=1; i<pictures.size(); i+=2){
			TableRow Schedule_row = new TableRow(mFragmentActivity);
			for(int j=0; j<2; j++){
				int index = i + j;
				View converView = myInflater.inflate(R.layout.poster_viewpage_item, null);
				
				TextView tv = (TextView)converView.findViewById(R.id.pager_context);
				ImageView iv = (ImageView)converView.findViewById(R.id.pager_poster);
				
				DisplayMetrics displayMetrics = new DisplayMetrics();
				((Activity) mFragmentActivity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
				int screenWidth = displayMetrics.widthPixels * 3 / 8;
		        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		        iv.getLayoutParams().height = (int)(screenWidth / 2);
		        iv.getLayoutParams().width = screenWidth;
		        
				if(index < pictures.size()) {
					tv.setText(pictures.get(index).getContent());
					tv.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_comment_small));
					RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
							(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					tv.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl_small), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl_small), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board));
					tv.setLayoutParams(rlParams);
					imageLoader.displayImage(pictures.get(index).getPicUrl(), iv, options);
					converView.setId(index);
					if(pictures.get(index).getTypeId() == 1) {
						converView.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View arg0) {
								Intent newAct = new Intent();
								int index = arg0.getId();
								newAct.setClass(mFragmentActivity, NewsContentTabletActivity.class );
							
					            Bundle bundle = new Bundle();
					            bundle.putInt("featureId", pictures.get(index).getId());
					            bundle.putString("featureName", pictures.get(index).getSource());
					            newAct.putExtras(bundle);
					            startActivity(newAct);
							}						
						});
					} else {
						converView.setClickable(false);
					}
				} else {
					tv.setVisibility(View.INVISIBLE);
				}
				
				TableRow.LayoutParams Params = new TableRow.LayoutParams
						(screenWidth, screenWidth / 2, 0.5f);
				if(index%2 != 0)
					Params.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width)/2, 
							0);
				else
					Params.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width)/2, 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
							0);
				converView.setLayoutParams(Params);
				converView.setBackgroundResource(R.drawable.item_background);
				Schedule_row.addView(converView);
			}
			Schedule_row.setLayoutParams(new LayoutParams
					(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			llFeature.addView(Schedule_row);
		}
	}
	
	class LoadPictureTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		pbInit.setVisibility(View.VISIBLE);
    		imageButtonRefresh.setVisibility(View.GONE);
    		super.onPreExecute();  
        }  
          
        @Override  
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	fetchPictureData();
            return "progress end";  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	pbInit.setVisibility(View.GONE);
        	if(pictures != null && pictures.size() > 0){
        		setPictureView();                
        		imageButtonRefresh.setVisibility(View.GONE);
        	} else {         
        		imageButtonRefresh.setVisibility(View.VISIBLE);
        	}
        	super.onPostExecute(result);
        }
    }
}
