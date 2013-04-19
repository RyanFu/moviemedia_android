package com.jumplife.tabletfragment;

import java.util.ArrayList;

import com.jumplife.movienews.NewsPhoneActivity;
import com.jumplife.movienews.PicturesPhoneActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;
import com.jumplife.movienews.entity.Picture;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class FeatureTabletFragment extends Fragment {	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private LinearLayout llFeature;
	
	//private ArrayList<Picture> pictures;
	private ArrayList<News> news;
	
	private LoadPictureTask loadPictureTask;
	
	Context mContext;

    @Override
    public void onAttach(Activity activity) {
        mContext = getActivity();
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
		/*
		pictures = new ArrayList<Picture>();
		pictures = fakePictures();
		*/
	}
	/*
	private ArrayList<Picture> fakePictures() {
		Picture tmp1 = new Picture(11, 1, "手工彩繪Star wars 所有人物", "http://pic.pimg.tw/jumplives/1364382592-2675134844.jpg?v=1364382593", "");
		Picture tmp2 = new Picture(22, 2, "阿凡達幕後", "http://pic.pimg.tw/jumplives/1364382592-3648714962.jpg?v=1364382593", "");
		ArrayList<Picture> tmps = new ArrayList<Picture>();
		tmps.add(tmp1);
		tmps.add(tmp2);
		return tmps;
	}
	*/
	@SuppressWarnings("deprecation")
	private void setPictureView() {
		LayoutInflater myInflater = LayoutInflater.from(mContext);
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		if(news.size() > 0) {
			//TableRow Schedule_row = new TableRow(getActivity());
			View converView = myInflater.inflate(R.layout.poster_viewpage_item, null);
			TextView tv = (TextView)converView.findViewById(R.id.pager_context);
			ImageView iv = (ImageView)converView.findViewById(R.id.pager_poster);
				
			DisplayMetrics displayMetrics = new DisplayMetrics();
			((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	        int screenWidth = displayMetrics.widthPixels;
	        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
	        iv.getLayoutParams().height = (int)(screenWidth * 4 / 5);
	        iv.getLayoutParams().width = screenWidth;
		        
				
			tv.setText(news.get(0).getName());
			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
					(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			tv.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.text_board), 
					mContext.getResources().getDimensionPixelSize(R.dimen.text_board), 
					mContext.getResources().getDimensionPixelSize(R.dimen.text_board), 
					mContext.getResources().getDimensionPixelSize(R.dimen.text_board));
			tv.setLayoutParams(rlParams);
			imageLoader.displayImage(news.get(0).getPosterUrl(), iv, options);
			converView.setId(0);
			converView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Intent newAct = new Intent();
					int index = arg0.getId();
					Log.d("", "index : " + index + " type id : " + news.get(index).getCategory().getTypeId());
					if(news.get(index).getCategory().getTypeId() == 1)
						newAct.setClass(mContext, NewsPhoneActivity.class );
					else
						newAct.setClass(mContext, PicturesPhoneActivity.class );
		            Bundle bundle = new Bundle();
		            bundle.putInt("categoryId", news.get(index).getCategory().getId());
		            bundle.putString("categoryName", news.get(index).getCategory().getName());
		            bundle.putInt("typeId", news.get(index).getCategory().getTypeId());
		            newAct.putExtras(bundle);
		            startActivity(newAct);
				}						
			});
			
			TableRow.LayoutParams Params = new TableRow.LayoutParams
					(screenWidth, screenWidth * 3 / 4, 1.0f);
			Params.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.item_board), 
					mContext.getResources().getDimensionPixelSize(R.dimen.item_board), 
					mContext.getResources().getDimensionPixelSize(R.dimen.item_board), 
					mContext.getResources().getDimensionPixelSize(R.dimen.item_board));
			converView.setLayoutParams(Params);
			/*Schedule_row.addView(converView);
			
			Schedule_row.setLayoutParams(new LayoutParams
					(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));*/
			llFeature.addView(converView);
		}
			
		for(int i=1; i<news.size(); i+=2){
			TableRow Schedule_row = new TableRow(mContext);
			for(int j=0; j<2; j++){
				int index = i + j;
				View converView = myInflater.inflate(R.layout.poster_viewpage_item, null);
				
				TextView tv = (TextView)converView.findViewById(R.id.pager_context);
				ImageView iv = (ImageView)converView.findViewById(R.id.pager_poster);
				
				DisplayMetrics displayMetrics = new DisplayMetrics();
				((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		        int screenWidth = displayMetrics.widthPixels;
		        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		        iv.getLayoutParams().height = (int)(screenWidth * 4 / 5);
		        iv.getLayoutParams().width = screenWidth;
		        
				if(index < news.size()) {
					tv.setText(news.get(index).getName());
					RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
							(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					tv.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.text_board), 
							mContext.getResources().getDimensionPixelSize(R.dimen.text_board), 
							mContext.getResources().getDimensionPixelSize(R.dimen.text_board), 
							mContext.getResources().getDimensionPixelSize(R.dimen.text_board));
					tv.setLayoutParams(rlParams);
					imageLoader.displayImage(news.get(index).getPosterUrl(), iv, options);
					converView.setId(index);
					converView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View arg0) {
							Intent newAct = new Intent();
							int index = arg0.getId();
							Log.d("", "index : " + index + " type id : " + news.get(index).getCategory().getTypeId());
							if(news.get(index).getCategory().getTypeId() == 1)
								newAct.setClass(mContext, NewsPhoneActivity.class );
							else
								newAct.setClass(mContext, PicturesPhoneActivity.class );
				            Bundle bundle = new Bundle();
				            bundle.putInt("categoryId", news.get(index).getCategory().getId());
				            bundle.putString("categoryName", news.get(index).getCategory().getName());
				            bundle.putInt("typeId", news.get(index).getCategory().getTypeId());
				            newAct.putExtras(bundle);
				            startActivity(newAct);
						}						
					});
				}
				
				TableRow.LayoutParams Params = new TableRow.LayoutParams
						(screenWidth / 2, screenWidth * 3 / 8, 0.5f);
				Params.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.item_board), 
						mContext.getResources().getDimensionPixelSize(R.dimen.item_board), 
						mContext.getResources().getDimensionPixelSize(R.dimen.item_board), 
						mContext.getResources().getDimensionPixelSize(R.dimen.item_board));
				converView.setLayoutParams(Params);
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
