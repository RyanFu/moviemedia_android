package com.jumplife.phonefragment;

import java.util.ArrayList;


import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlLayout.ViewAdRunnable;
import com.google.analytics.tracking.android.EasyTracker;
import com.hodo.HodoADView;
import com.hodo.listener.HodoADListener;
import com.jumplife.adapter.PosterViewPagerAdapter;
import com.jumplife.movienews.AboutUsActivity;
import com.jumplife.movienews.NewsPhoneActivity;
import com.jumplife.movienews.PicturesPhoneActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;
import com.jumplife.movienews.entity.NewsCategory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
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

public class OverViewPhoneFragment extends Fragment implements AdWhirlInterface{	
	
	private View fragmentView;
	private TextView topbar_text;
	private ImageButton imageButtonRefresh;
	private ImageButton imageButtonRefreshLand;
	private ImageButton imageButtonAbourUs;
	private RelativeLayout rlViewpager;
	private LinearLayout llFeature;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	private ArrayList<NewsCategory> newsCategories;
	//private ArrayList<Picture> pictures;
	
	private ArrayList<News> editorSelectedNewsList;
	
	private PosterViewPagerAdapter mAdapter;
	private LoadCategoryTask loadCategoryTask;
	private LoadPictureTask loadPictureTask;
	private ProgressBar pbInit;
	
	//for ad
	RelativeLayout adLayout;
	private AdWhirlLayout adWhirlLayout;
	private FragmentActivity mFragmentActivity;
	
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
	    
	    loadPictureTask = new LoadPictureTask();
	    if(Build.VERSION.SDK_INT < 11)
	    	loadPictureTask.execute();
        else
        	loadPictureTask.executeOnExecutor(LoadPictureTask.THREAD_POOL_EXECUTOR, 0);
	    

		AdTask adTask = new AdTask();
		adTask.execute();
	    
		return fragmentView;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
	
	private void initView() {
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_overview);
		topbar_text = (TextView)fragmentView.findViewById(R.id.topbar_text);
		rlViewpager = (RelativeLayout)fragmentView.findViewById(R.id.rl_viewpager);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		imageButtonRefreshLand = (ImageButton)fragmentView.findViewById(R.id.refresh_land);
		imageButtonAbourUs = (ImageButton)fragmentView.findViewById(R.id.ib_about_us);
		llFeature = (LinearLayout)fragmentView.findViewById(R.id.ll_feature);
		mPager = (ViewPager)fragmentView.findViewById(R.id.pager);
		mIndicator = (CirclePageIndicator)fragmentView.findViewById(R.id.indicator);
		adLayout = (RelativeLayout)fragmentView.findViewById(R.id.ad_layout);
		
		topbar_text.setText(mFragmentActivity.getResources().getString(R.string.app_name));
		
		imageButtonRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadCategoryTask = new LoadCategoryTask();
        	    if(Build.VERSION.SDK_INT < 11)
        	    	loadCategoryTask.execute();
                else
                	loadCategoryTask.executeOnExecutor(LoadCategoryTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
		
		imageButtonRefreshLand.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadPictureTask = new LoadPictureTask();
        	    if(Build.VERSION.SDK_INT < 11)
        	    	loadPictureTask.execute();
                else
                	loadPictureTask.executeOnExecutor(LoadPictureTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
		
		imageButtonAbourUs.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	Intent newAct = new Intent();
				newAct.setClass(mFragmentActivity, AboutUsActivity.class );
	            startActivity(newAct);
            }
        });
	}
	
	private void fetchCategoryData() {
		NewsAPI api = new NewsAPI();
		newsCategories = api.getCategoryList();
		if (newsCategories == null) {
			//error handling
		}
	}
	@SuppressWarnings("deprecation")
	private void setCategory() {
		LayoutInflater myInflater = LayoutInflater.from(mFragmentActivity);
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer
				((int)this.getResources().getDimensionPixelSize(R.dimen.overview_category_item_bg_radius)))
		.build();
		
		for(int i=0; i<newsCategories.size(); i+=2){
			TableRow Schedule_row = new TableRow(mFragmentActivity);
			for(int j=0; j<2; j++){
				int index = i + j;
				View converView = myInflater.inflate(R.layout.item_category, null);
				
				TextView tv = (TextView)converView.findViewById(R.id.pager_context);
				ImageView iv = (ImageView)converView.findViewById(R.id.pager_poster);
				RelativeLayout rl = (RelativeLayout)converView.findViewById(R.id.rl_overview_category);
				
				DisplayMetrics displayMetrics = new DisplayMetrics();
				mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		        int screenWidth = displayMetrics.widthPixels;
		        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		        iv.getLayoutParams().height = (int)(screenWidth * 4 / 5);
		        iv.getLayoutParams().width = screenWidth;
		        iv.setBackgroundResource(R.drawable.overview_category_item_poster_background);
		        
				if(index < newsCategories.size()) {
					tv.setText(newsCategories.get(index).getName());
					RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
							(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					rlParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					tv.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board));
					tv.setLayoutParams(rlParams);
					imageLoader.displayImage(newsCategories.get(index).getPosterUrl(), iv, options);
					converView.setId(index);
					converView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View arg0) {
							Intent newAct = new Intent();
							int index = arg0.getId();
							//Log.d("", "index : " + index + " type id : " + newsCategories.get(index).getTypeId());
							if(newsCategories.get(index).getTypeId() == 1)
								newAct.setClass(mFragmentActivity, NewsPhoneActivity.class );
							else
								newAct.setClass(mFragmentActivity, PicturesPhoneActivity.class );
				            Bundle bundle = new Bundle();
				            bundle.putInt("categoryId", newsCategories.get(index).getId());
				            bundle.putString("categoryName", newsCategories.get(index).getName());
				            bundle.putInt("typeId", newsCategories.get(index).getTypeId());
				            newAct.putExtras(bundle);
				            
				            EasyTracker.getTracker().sendEvent("精選主題", "點擊", "類別id: " +  newsCategories.get(index).getId() + 
				            		" ; 類別名稱: " + newsCategories.get(index).getName(), (long) 0);
				            
				            startActivity(newAct);
						}						
					});
				} else {
					tv.setVisibility(View.INVISIBLE);
					iv.setVisibility(View.INVISIBLE);
					converView.setVisibility(View.INVISIBLE);
				}
				
				rl.setBackgroundResource(R.drawable.overview_category_item_background);
				
				TableRow.LayoutParams Params = new TableRow.LayoutParams
						(screenWidth / 2, screenWidth * 3 / 8, 0.5f);
				if(index%2 != 1)
					Params.setMargins(0, 
						mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
						mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width)/2,
						0);
				else
					Params.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width)/2, 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
							0, 
							0);
				converView.setLayoutParams(Params);
				converView.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_bg_board_and_paddijng),
						mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_bg_board_and_paddijng), 
						mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_bg_board_and_paddijng),
						mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_bg_board_and_paddijng));
				converView.setBackgroundResource(R.drawable.overview_category_item_background);
				Schedule_row.addView(converView);
			}
			Schedule_row.setLayoutParams(new LayoutParams
					(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			llFeature.addView(Schedule_row);
		}
	}
	
	class LoadCategoryTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		imageButtonRefresh.setVisibility(View.GONE);
    		pbInit.setVisibility(View.VISIBLE);
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
        	if(newsCategories != null && newsCategories.size() > 0){
        		setCategory();
        		imageButtonRefresh.setVisibility(View.GONE);		
    		} else
                imageButtonRefresh.setVisibility(View.VISIBLE);
        	super.onPostExecute(result);
        }
    }
	
	
	private void fetchPictureData() {
		NewsAPI api = new NewsAPI();
		editorSelectedNewsList = api.getEditorSelectedList();
		if (editorSelectedNewsList == null) {
			//error handling
		}
	}
	

	private void setPictureView() {
		mAdapter = new PosterViewPagerAdapter(mFragmentActivity, editorSelectedNewsList);

        mPager.setAdapter(mAdapter);
        mPager.setVisibility(View.VISIBLE);
        
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        LayoutParams rlLayout = new LayoutParams(screenWidth, screenWidth / 2);
        
        rlViewpager.setLayoutParams(rlLayout);
        
        mIndicator.setViewPager(mPager);
	}
	
	private void setRefresh() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        
        LayoutParams rlLayout = new LayoutParams(screenWidth, screenWidth / 2);
        
        rlViewpager.setLayoutParams(rlLayout);
        mPager.setVisibility(View.GONE);
	}
	
	class LoadPictureTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {                
            imageButtonRefreshLand.setVisibility(View.GONE);
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
        	if(editorSelectedNewsList != null && editorSelectedNewsList.size() > 0){
        		setPictureView();                
                imageButtonRefreshLand.setVisibility(View.GONE);
        	} else {
        		setRefresh();                
                imageButtonRefreshLand.setVisibility(View.VISIBLE);
        	}
        	super.onPostExecute(result);
        }
    }
	
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance().activityStart(mFragmentActivity); // Add this method.
		EasyTracker.getTracker().sendView("手機首頁Fragment");
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(mFragmentActivity); // Add this method
	}
	
	public void setAd() {
    	
    	Resources res = mFragmentActivity.getResources();
    	String adwhirlKey = res.getString(R.string.adwhirl_key);
    	AdWhirlManager.setConfigExpireTimeout(1000 * 30); 

        AdWhirlTargeting.setTestMode(false);
   		
        adWhirlLayout = new AdWhirlLayout(mFragmentActivity, adwhirlKey);	
        
        adWhirlLayout.setAdWhirlInterface(this);
    	
        adWhirlLayout.setGravity(Gravity.CENTER_HORIZONTAL);
	 	
    	adLayout.addView(adWhirlLayout);
    }
	
	public void showHodoAd() {
    	Resources res = mFragmentActivity.getResources();
    	String hodoKey = res.getString(R.string.hodo_key);
    	AdWhirlManager.setConfigExpireTimeout(1000 * 30); 
		final HodoADView hodoADview = new HodoADView(mFragmentActivity);
        hodoADview.reruestAD(hodoKey);
        //關掉自動輪撥功能,交由adWhirl輪撥
        hodoADview.setAutoRefresh(false);
        
        hodoADview.setListener(new HodoADListener() {
            public void onGetBanner() {
                //成功取得banner
            	//Log.d("hodo", "onGetBanner");
		        adWhirlLayout.adWhirlManager.resetRollover();
	            adWhirlLayout.handler.post(new ViewAdRunnable(adWhirlLayout, hodoADview));
	            adWhirlLayout.rotateThreadedDelayed();
            }
            public void onFailed(String msg) {
                //失敗取得banner
                //Log.d("hodo", "onFailed :" +msg);
                adWhirlLayout.rollover();
            }
            public void onBannerChange(){
                //banner 切換
                //Log.d("hodo", "onBannerChange");
            }
        });
    }
	
	class AdTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... arg0) {
			
			return null;
		}
		 @Override  
	     protected void onPostExecute(String result) {
			 setAd();
			 super.onPostExecute(result);
		 }
    }

	@Override
	public void adWhirlGeneric() {
		// TODO Auto-generated method stub
		
	}
}
