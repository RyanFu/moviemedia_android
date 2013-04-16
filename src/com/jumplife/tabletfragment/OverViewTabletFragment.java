package com.jumplife.tabletfragment;

import java.util.ArrayList;

import com.jumplife.movienews.R;
import com.jumplife.movienews.entity.NewsCategories;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverViewTabletFragment extends Fragment {	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private LinearLayout llFeature;
	private ArrayList<NewsCategories> newsCategories;
	private ArrayList<View> viewFeatures;
	private LoadCategoryTask loadCategoryTask;
	
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
		newsCategories = new ArrayList<NewsCategories>();
		newsCategories = fakeDataCategories();
	}
	
	private ArrayList<NewsCategories> fakeDataCategories() {
		NewsCategories tmp0 = new NewsCategories(0, "編輯每日精選", "http://pic.pimg.tw/jumplives/1364376965-2619884891.jpg",
				"http://pic.pimg.tw/jumplives/1365507641-1602607809.gif", 0);
		NewsCategories tmp1 = new NewsCategories(1, "電影新星聞", "http://pic.pimg.tw/jumplives/1364376965-2619884891.jpg",
				"http://pic.pimg.tw/jumplives/1365507641-365405581.gif", 1);
		NewsCategories tmp2 = new NewsCategories(2,	"電影名言", "http://pic.pimg.tw/jumplives/1364376966-1338225628.jpg?v=1364376967	",
				"http://pic.pimg.tw/jumplives/1365507641-3366255340.gif", 2);
		ArrayList<NewsCategories> tmps = new ArrayList<NewsCategories>();
		tmps.add(tmp0);
		tmps.add(tmp1);
		tmps.add(tmp2);
			
		return tmps;
	}
	
	private void setViewPress(int pos) {
		Log.d(null, "size : " + viewFeatures.size() + " pos : " + pos);
		for(int i=0; i<viewFeatures.size(); i++) {
			View tmp = viewFeatures.get(i);
			View seperate = (View)tmp.findViewById(R.id.feature_seperate);
			if(i == pos) {
				seperate.setVisibility(View.VISIBLE);
				tmp.setBackgroundResource(R.color.feature_press);
			} else {
				seperate.setVisibility(View.INVISIBLE);
				tmp.setBackgroundResource(R.color.feature_normal);
			}
		}
	}
	
	private void setCategory() {
		viewFeatures = new ArrayList<View>();
		
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		LayoutInflater myInflater = LayoutInflater.from(mFragmentActivity);
		for(int i=0; i<newsCategories.size(); i+=1){
			View converView = myInflater.inflate(R.layout.item_feature, null);
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
					((int) (mFragmentActivity.getResources().getDimension(R.dimen.feature_name)),
							(int) (mFragmentActivity.getResources().getDimension(R.dimen.feature_name)));
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
			tvParams.setMargins((mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_board_rl) / 4), 
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
					
					if(newsCategories.get(index).getTypeId() == 0) {
						
						FeatureTabletFragment features = new FeatureTabletFragment(); 

	                    FragmentTransaction ft = getFragmentManager()
	                            .beginTransaction();
	                    ft.replace(R.id.details, features);//将得到的fragment 替换当前的viewGroup内容，add则不替换会依次累加
	                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);//设置动画效果
	                    ft.commit();//提交
		                		                
					} else if(newsCategories.get(index).getTypeId() == 1) {
						
						NewsTabletFragment news = NewsTabletFragment.NewInstance(newsCategories.get(index).getId(),
		                			newsCategories.get(index).getName()); 

	                    FragmentTransaction ft = getFragmentManager()
	                            .beginTransaction();
	                    ft.replace(R.id.details, news);//将得到的fragment 替换当前的viewGroup内容，add则不替换会依次累加
	                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);//设置动画效果
	                    ft.commit();//提交
		                		                
					} else if(newsCategories.get(index).getTypeId() == 2) {
						
						PicturesTabletFragment pics = PicturesTabletFragment.NewInstance(newsCategories.get(index).getId(),
		                			newsCategories.get(index).getName()); 

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
}
