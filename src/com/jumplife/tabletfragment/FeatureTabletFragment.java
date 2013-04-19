package com.jumplife.tabletfragment;

import java.util.ArrayList;

import com.jumplife.movienews.NewsContentTabletActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

public class FeatureTabletFragment extends Fragment {	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private LinearLayout llFeature;
	
	//private ArrayList<Picture> pictures;
	private ArrayList<News> news;
	
	private LoadPictureTask loadPictureTask;
	private ProgressBar pbInit;
	
	private FragmentActivity mFragmentActivity;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

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
		NewsAPI api = new NewsAPI();
		news = api.getEditorSelectedList();
	}

	@SuppressWarnings("deprecation")
	private void setPictureView() {
		LayoutInflater myInflater = LayoutInflater.from(mFragmentActivity);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		if(news.size() > 0) {
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
		        
				
			tv.setText(news.get(0).getName());
			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
					(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			tv.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board));
			tv.setLayoutParams(rlParams);
			imageLoader.displayImage(news.get(0).getPosterUrl(), iv, options);
			converView.setId(0);

			converView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					int index = arg0.getId();
					if(news.get(0).getCategory().getTypeId() == 1) {
						Intent newAct = new Intent();
						newAct.setClass(mFragmentActivity, NewsContentTabletActivity.class );
					
			            Bundle bundle = new Bundle();
	
			            bundle.putInt("newsId", news.get(index).getId());
			            bundle.putString("categoryName", news.get(index).getCategory().getName());
			            bundle.putString("releaseDateStr", NewsAPI.dateToString(news.get(index).getReleaseDate()));
			            bundle.putString("origin", news.get(index).getOrigin());
			            bundle.putString("name", news.get(index).getName());
			            
			            newAct.putExtras(bundle);
			            startActivity(newAct);
					} else {
						Dialog dialog = new Dialog(mFragmentActivity);
						dialog.setContentView(R.layout.dialog_picture);
						ImageView ivPicture = (ImageView)dialog.findViewById(R.id.iv_picture);
						RelativeLayout.LayoutParams ivrlParams = new RelativeLayout.LayoutParams
								(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
						ivPicture.setLayoutParams(ivrlParams);
						ivPicture.setScaleType(ScaleType.FIT_CENTER);
						
						DisplayImageOptions optionsPic = new DisplayImageOptions.Builder()
						.cacheInMemory()
						.cacheOnDisc()
						.displayer(new SimpleBitmapDisplayer())
						.build();
						imageLoader.displayImage(news.get(index).show(), ivPicture, optionsPic);
						dialog.show();
					} 
				}						
			});
			
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
			

		for(int i=1; i<news.size(); i+=2){
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
		        

				if(index < news.size()) {
					tv.setText(news.get(index).getName());
					tv.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_comment_small));

					RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
							(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					tv.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl_small), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl_small), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board));
					tv.setLayoutParams(rlParams);
					imageLoader.displayImage(news.get(index).getPosterUrl(), iv, options);
					converView.setId(index);

					if(news.get(index).getCategory().getTypeId() == 1) {
						converView.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View arg0) {
								Intent newAct = new Intent();
								int index = arg0.getId();
								newAct.setClass(mFragmentActivity, NewsContentTabletActivity.class );
							
								Bundle bundle = new Bundle();
						        bundle.putInt("categoryId", news.get(index).getCategory().getId());
						        bundle.putString("categoryName", news.get(index).getCategory().getName());
						        bundle.putInt("typeId", news.get(index).getCategory().getTypeId());
						        newAct.putExtras(bundle);
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
        	if(news != null && news.size() > 0){

        		setPictureView();                
        		imageButtonRefresh.setVisibility(View.GONE);
        	} else {         
        		imageButtonRefresh.setVisibility(View.VISIBLE);
        	}
        	super.onPostExecute(result);
        }
    }
}
