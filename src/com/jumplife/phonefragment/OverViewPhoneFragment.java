package com.jumplife.phonefragment;

import java.util.ArrayList;

import com.jumplife.adapter.PosterViewPagerAdapter;
import com.jumplife.movienews.NewsPhoneActivity;
import com.jumplife.movienews.PicturesPhoneActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.entity.NewsCategories;
import com.jumplife.movienews.entity.Picture;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class OverViewPhoneFragment extends Fragment {	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private ImageButton imageButtonRefreshLand;
	private RelativeLayout rlViewpager;
	private LinearLayout llFeature;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	private ArrayList<NewsCategories> newsCategories;
	private ArrayList<Picture> pictures;
	private PosterViewPagerAdapter mAdapter;
	private LoadCategoryTask loadCategoryTask;
	private LoadPictureTask loadPictureTask;
	
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
	    
		return fragmentView;
	}
	
	private void initView() {
		rlViewpager = (RelativeLayout)fragmentView.findViewById(R.id.rl_viewpager);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		imageButtonRefreshLand = (ImageButton)fragmentView.findViewById(R.id.refresh_land);
		llFeature = (LinearLayout)fragmentView.findViewById(R.id.ll_feature);
		mPager = (ViewPager)fragmentView.findViewById(R.id.pager);
		mIndicator = (CirclePageIndicator)fragmentView.findViewById(R.id.indicator);
        
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
	}
	
	private void fetchCategoryData() {
		newsCategories = new ArrayList<NewsCategories>();
		newsCategories = fakeDataCategories();
	}
	
	private ArrayList<NewsCategories> fakeDataCategories() {
		NewsCategories tmp1 = new NewsCategories(1, "電影新星聞", "http://pic.pimg.tw/jumplives/1364376965-2619884891.jpg", 1);
		NewsCategories tmp2 = new NewsCategories(2,	"電影名言", "http://pic.pimg.tw/jumplives/1364376966-1338225628.jpg?v=1364376967	", 2);
		ArrayList<NewsCategories> tmps = new ArrayList<NewsCategories>();
		tmps.add(tmp1);
		tmps.add(tmp2);
		tmps.add(tmp1);
		tmps.add(tmp2);
		tmps.add(tmp1);
		tmps.add(tmp2);
		
		return tmps;
	}
	
	@SuppressWarnings("deprecation")
	private void setCategory() {
		LayoutInflater myInflater = LayoutInflater.from(getActivity());
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		for(int i=0; i<newsCategories.size(); i+=2){
			TableRow Schedule_row = new TableRow(getActivity());
			for(int j=0; j<2; j++){
				int index = i + j;
				View converView = myInflater.inflate(R.layout.poster_viewpage_item, null);
				
				TextView tv = (TextView)converView.findViewById(R.id.pager_context);
				ImageView iv = (ImageView)converView.findViewById(R.id.pager_poster);
				
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		        int screenWidth = displayMetrics.widthPixels;
		        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		        iv.getLayoutParams().height = (int)(screenWidth * 4 / 5);
		        iv.getLayoutParams().width = screenWidth;
		        
				if(index < newsCategories.size()) {
					tv.setText(newsCategories.get(index).getName());
					RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
							(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					tv.setPadding(getActivity().getResources().getDimensionPixelSize(R.dimen.text_board), 
							getActivity().getResources().getDimensionPixelSize(R.dimen.text_board), 
							getActivity().getResources().getDimensionPixelSize(R.dimen.text_board), 
							getActivity().getResources().getDimensionPixelSize(R.dimen.text_board));
					tv.setLayoutParams(rlParams);
					imageLoader.displayImage(newsCategories.get(index).getPosterUrl(), iv, options);
					converView.setId(index);
					converView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View arg0) {
							Intent newAct = new Intent();
							int index = arg0.getId();
							Log.d("", "index : " + index + " type id : " + newsCategories.get(index).getTypeId());
							if(newsCategories.get(index).getTypeId() == 1)
								newAct.setClass(getActivity(), NewsPhoneActivity.class );
							else
								newAct.setClass(getActivity(), PicturesPhoneActivity.class );
				            Bundle bundle = new Bundle();
				            bundle.putInt("featureId", newsCategories.get(index).getId());
				            bundle.putString("featureName", newsCategories.get(index).getName());
				            newAct.putExtras(bundle);
				            startActivity(newAct);
						}						
					});
				}
				
				TableRow.LayoutParams Params = new TableRow.LayoutParams
						(screenWidth / 2, screenWidth * 3 / 8, 0.5f);
				if(index%2 != 1)
					Params.setMargins(getActivity().getResources().getDimensionPixelSize(R.dimen.item_board), 
						getActivity().getResources().getDimensionPixelSize(R.dimen.item_board), 
						getActivity().getResources().getDimensionPixelSize(R.dimen.item_board)/2, 
						getActivity().getResources().getDimensionPixelSize(R.dimen.item_board));
				else
					Params.setMargins(getActivity().getResources().getDimensionPixelSize(R.dimen.item_board)/2, 
							getActivity().getResources().getDimensionPixelSize(R.dimen.item_board), 
							getActivity().getResources().getDimensionPixelSize(R.dimen.item_board), 
							getActivity().getResources().getDimensionPixelSize(R.dimen.item_board));
				converView.setLayoutParams(Params);
				converView.setBackgroundResource(R.drawable.item_background);
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
        	if(newsCategories != null && newsCategories.size() > 0){
        		setCategory();
        		imageButtonRefresh.setVisibility(View.GONE);		
    		} else
                imageButtonRefresh.setVisibility(View.VISIBLE);
        	super.onPostExecute(result);
        }
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
	
	private void setPictureView() {
		mAdapter = new PosterViewPagerAdapter(getActivity(), pictures);

        mPager.setAdapter(mAdapter);
        mPager.setVisibility(View.VISIBLE);
        
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        LayoutParams rlLayout = new LayoutParams(screenWidth, screenWidth / 2);
        
        rlViewpager.setLayoutParams(rlLayout);
        
        mIndicator.setViewPager(mPager);
	}
	
	private void setRefresh() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        LayoutParams rlLayout = new LayoutParams(screenWidth, screenWidth / 2);
        
        rlViewpager.setLayoutParams(rlLayout);
        mPager.setVisibility(View.GONE);
	}
	
	class LoadPictureTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
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
        	if(pictures != null && pictures.size() > 0){
        		setPictureView();                
                imageButtonRefreshLand.setVisibility(View.GONE);
        	} else {
        		setRefresh();                
                imageButtonRefreshLand.setVisibility(View.VISIBLE);
        	}
        	super.onPostExecute(result);
        }
    }
}
