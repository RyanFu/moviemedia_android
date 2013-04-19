package com.jumplife.tabletfragment;

import java.util.ArrayList;

import com.jumplife.movienews.R;
import com.jumplife.movienews.entity.NewsCategory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverViewTabletFragment extends Fragment {	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private LinearLayout llFeature;
	private ArrayList<NewsCategory> newsCategories;
	private ArrayList<View> viewFeatures;
	private LoadCategoryTask loadCategoryTask;
	
	private Context mContext;

    @Override
    public void onAttach(Activity activity) {
        mContext = getActivity();
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
		newsCategories = new ArrayList<NewsCategory>();
		newsCategories = fakeDataCategories();
	}
	
	private ArrayList<NewsCategory> fakeDataCategories() {
		NewsCategory tmp0 = new NewsCategory(0, "編輯每日精選", "http://pic.pimg.tw/jumplives/1364376965-2619884891.jpg", "", 0);
		NewsCategory tmp1 = new NewsCategory(1, "電影新星聞", "http://pic.pimg.tw/jumplives/1364376965-2619884891.jpg", "", 1);
		NewsCategory tmp2 = new NewsCategory(2,	"電影名言", "http://pic.pimg.tw/jumplives/1364376966-1338225628.jpg?v=1364376967	", "", 2);
		ArrayList<NewsCategory> tmps = new ArrayList<NewsCategory>();
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
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		LayoutInflater myInflater = LayoutInflater.from(mContext);
		for(int i=0; i<newsCategories.size(); i+=1){
			View converView = myInflater.inflate(R.layout.item_feature, null);
			TextView tv = (TextView)converView.findViewById(R.id.feature_name);
			ImageView iv = (ImageView)converView.findViewById(R.id.feature_pic);
			
	        tv.setText(newsCategories.get(i).getName());					
			imageLoader.displayImage(newsCategories.get(i).getPosterUrl(), iv, options);
			
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
		
		FeatureTabletFragment features = new FeatureTabletFragment(); 

        FragmentTransaction ft = getFragmentManager()
                .beginTransaction();
        ft.add(R.id.details, features);//将得到的fragment 替换当前的viewGroup内容，add则不替换会依次累加
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);//设置动画效果
        ft.commit();//提交
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
