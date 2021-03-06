package com.jumplife.tabletfragment;

import java.util.ArrayList;


import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.NewsCategory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverViewTabletFragment extends Fragment{	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private LinearLayout llFeature;
	private ArrayList<NewsCategory> newsCategories;
	private ArrayList<NewsCategory> serverNewsCategories;
	private ArrayList<View> viewFeatures;
	private LoadCategoryTask loadCategoryTask;
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
		
		fragmentView = inflater.inflate(R.layout.fragment_overview, container, false);		
		initView();
		
		loadCategoryTask = new LoadCategoryTask();
	    if(Build.VERSION.SDK_INT < 11)
	    	loadCategoryTask.execute();
        else
        	loadCategoryTask.executeOnExecutor(LoadCategoryTask.THREAD_POOL_EXECUTOR, 0);

	    return fragmentView;
	}
	
	private void initView() {
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_overview);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		llFeature = (LinearLayout)fragmentView.findViewById(R.id.ll_feature);
        
		imageButtonRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadCategoryTask = new LoadCategoryTask();
        	    if(Build.VERSION.SDK_INT < 11)
        	    	loadCategoryTask.execute();
                else
                	loadCategoryTask.executeOnExecutor(LoadCategoryTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
	}
	
	private void fetchCategoryData() {
		newsCategories = new ArrayList<NewsCategory>(10);
		
		NewsCategory editorCHoice = new NewsCategory(-1, "編輯精選", "", "", 0);
		newsCategories.add(editorCHoice);
		
		NewsAPI api = new NewsAPI();
		serverNewsCategories = api.getCategoryList();
	}
	
	private void setViewPress(int pos) {
		Log.d(null, "size : " + viewFeatures.size() + " pos : " + pos);
		for(int i=0; i<viewFeatures.size(); i++) {
			View tmp = viewFeatures.get(i);
			View seperate = (View)tmp.findViewById(R.id.feature_seperate);
			TextView tv  = (TextView)tmp.findViewById(R.id.feature_name);
			if(i == pos) {
				seperate.setVisibility(View.VISIBLE);
				tv.setTextColor(mFragmentActivity.getResources().getColor(R.color.feature_tv_press));
				tmp.setBackgroundResource(R.color.feature_press);
			} else {
				seperate.setVisibility(View.INVISIBLE);
				tv.setTextColor(mFragmentActivity.getResources().getColor(R.color.feature_tv_normal));
				tmp.setBackgroundResource(R.color.feature_normal);
			}
		}
	}
	
	private void setCategory() {
		viewFeatures = new ArrayList<View>();
		
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.bitmapConfig(Bitmap.Config.RGB_565)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		LayoutInflater myInflater = LayoutInflater.from(mFragmentActivity);
		for(int i=0; i<newsCategories.size(); i+=1){
			View converView = myInflater.inflate(R.layout.item_categorye, null);
			View seperate = (View)converView.findViewById(R.id.feature_seperate);
			TextView tv = (TextView)converView.findViewById(R.id.feature_name);
			ImageView iv = (ImageView)converView.findViewById(R.id.feature_pic);
	        
			RelativeLayout.LayoutParams vParams = new RelativeLayout.LayoutParams
						(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.seperate),
								mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_name) +
								mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_board_tb) +
								mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_board_tb));
			vParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			seperate.setLayoutParams(vParams);
			
	        RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams
					((int) (mFragmentActivity.getResources().getDimension(R.dimen.overview_item_feature_iv_icon_tablet)),
							(int) (mFragmentActivity.getResources().getDimension(R.dimen.overview_item_feature_iv_icon_tablet)));
	        ivParams.addRule(RelativeLayout.RIGHT_OF, seperate.getId());
	        ivParams.addRule(RelativeLayout.CENTER_VERTICAL);
	        ivParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_board_rl), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_board_tb), 
					0, 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_board_tb));
	        iv.setLayoutParams(ivParams);
	        iv.setScaleType(ScaleType.FIT_CENTER);
			imageLoader.displayImage(newsCategories.get(i).getIconUrl(), iv, options);
			
			RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams
					(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tvParams.addRule(RelativeLayout.RIGHT_OF, iv.getId());
			tvParams.addRule(RelativeLayout.CENTER_VERTICAL);
			tvParams.setMargins((mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_board_rl) / 3), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_board_tb), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_board_rl), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_board_tb));
	        tv.setLayoutParams(tvParams);
	        tv.setText(newsCategories.get(i).getName());		
			
			converView.setId(i);
			converView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					
					int index = arg0.getId();
					setViewPress(index);
					
					EasyTracker.getTracker().sendEvent("精選主題", "點擊", "類別id: " +  newsCategories.get(index).getId() + 
		            		" ; 類別名稱: " + newsCategories.get(index).getName(), (long) 0);
					
					if(newsCategories.get(index).getTypeId() == 0) {
						
						FeatureTabletFragment features = new FeatureTabletFragment(); 

	                    FragmentTransaction ft = getFragmentManager()
	                            .beginTransaction();
	                    ft.replace(R.id.details, features);//将得到的fragment 替换当前的viewGroup内容，add则不替换会依次累加
	                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);//设置动画效果
	                    ft.commit();//提交
		                		                
					} else if(newsCategories.get(index).getTypeId() == 1) {
						
						NewsTabletFragment news = NewsTabletFragment.NewInstance(newsCategories.get(index).getId(),
	                			newsCategories.get(index).getName(), newsCategories.get(index).getTypeId()); 


	                    FragmentTransaction ft = getFragmentManager()
	                            .beginTransaction();
	                    ft.replace(R.id.details, news);//将得到的fragment 替换当前的viewGroup内容，add则不替换会依次累加
	                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);//设置动画效果
	                    ft.commit();//提交
		                		                
					} else if(newsCategories.get(index).getTypeId() == 2) {
						
						PicturesTabletFragment pics = PicturesTabletFragment.NewInstance(newsCategories.get(index).getId(),
		                			newsCategories.get(index).getName(), newsCategories.get(index).getTypeId()); 

	                    FragmentTransaction ft = getFragmentManager()
	                            .beginTransaction();
	                    ft.replace(R.id.details, pics);//将得到的fragment 替换当前的viewGroup内容，add则不替换会依次累加
	                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);//设置动画效果
	                    ft.commit();//提交
					}
				}
			});
			
			viewFeatures.add(converView);
			llFeature.addView(converView);
		}
		setViewPress(0);
	}
	
	class LoadCategoryTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		pbInit.setVisibility(View.VISIBLE);
    		imageButtonRefresh.setVisibility(View.GONE);
    		super.onPreExecute();  
        }  
          
        @Override  
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	fetchCategoryData();
            return "progress end";  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	pbInit.setVisibility(View.GONE);
        	if(serverNewsCategories != null && serverNewsCategories.size() > 0){
        		newsCategories.addAll(serverNewsCategories);
        		setCategory();
        		imageButtonRefresh.setVisibility(View.GONE);		
    		} else
                imageButtonRefresh.setVisibility(View.VISIBLE);
        	super.onPostExecute(result);
        }
    }
	
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance().activityStart(mFragmentActivity); // Add this method.
		EasyTracker.getTracker().sendView("平板首頁Fragment");
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(mFragmentActivity); // Add this method
	}

}
