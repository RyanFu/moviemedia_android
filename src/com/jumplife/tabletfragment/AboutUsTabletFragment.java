package com.jumplife.tabletfragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.AppProject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class AboutUsTabletFragment extends Fragment {	
	
	private View fragmentView;
	private TextView topbar_text;
	private TextView tvOtherFunc;
	private TextView tvOtherProductor;
	private TextView tvAboutAPP;
	private LinearLayout llAboutUsOther;
	private LinearLayout llAboutUsOtherProductor;
	private ProgressBar pbInit;
	private ArrayList<AppProject> appProject;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	private final int columnNum = 4;
	private LoadDataTask loadtask;
	private FragmentActivity mFragmentActivity;

    @Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_aboutus, container, false);		
		
		initView();
        loadtask = new LoadDataTask();
	    if(Build.VERSION.SDK_INT < 11)
	    	loadtask.execute();
        else
        	loadtask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
	    
		return fragmentView;
	}
	
	private void initView() {
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_about_us);
		topbar_text = (TextView)fragmentView.findViewById(R.id.topbar_text);
		tvOtherFunc = (TextView)fragmentView.findViewById(R.id.tv_aboutus_other_func);
		tvOtherProductor = (TextView)fragmentView.findViewById(R.id.tv_aboutus_other_productor);
		tvAboutAPP = (TextView)fragmentView.findViewById(R.id.tv_aboutapp_content);
		llAboutUsOther = (LinearLayout)fragmentView.findViewById(R.id.ll_aboutus_other);
		llAboutUsOtherProductor = (LinearLayout)fragmentView.findViewById(R.id.ll_aboutus_other_productor);
		
		topbar_text.setText(mFragmentActivity.getResources().getString(R.string.about_us));
		tvAboutAPP.setText(mFragmentActivity.getResources().getString(R.string.aboutapp_intro) +
				"\n\n\n＊＊＊" + 
				mFragmentActivity.getResources().getString(R.string.liability_disclaimer) + 
				"＊＊＊\n\n" +
				Html.fromHtml(mFragmentActivity.getResources().getString(R.string.aboutapp_declare)));
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = (int) displayMetrics.widthPixels * 13 / 40;
		tvOtherFunc.getLayoutParams().width = width;
		tvOtherProductor.getLayoutParams().width = width;		
		
				
		initBasicView();
	}
	
	private void initBasicView() {
		TableRow Schedule_row = new TableRow(mFragmentActivity);
				
		TextView tvFeed = new TextView(mFragmentActivity);
		ImageView ivFeed = new ImageView(mFragmentActivity);
		RelativeLayout rlFeed = new RelativeLayout(mFragmentActivity);		
			
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = (int) ((displayMetrics.widthPixels * 13 / 20
				- mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_aboutapp_margin) * 5.0f)
				/ 4 - mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin) * 2.0f);
		int height = width + 3 * mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin) 
				+ mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title);
        ivFeed.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams rlIvParams = new RelativeLayout.LayoutParams(width, width);
        rlIvParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rlIvParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlIvParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
        ivFeed.setImageResource(R.drawable.feedback);
        rlFeed.addView(ivFeed, rlIvParams);
		
		RelativeLayout.LayoutParams rlTvFeedParams = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rlTvFeedParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rlTvFeedParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rlTvFeedParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
		tvFeed.setText(mFragmentActivity.getResources().getString(R.string.advice_and_feedback));
		tvFeed.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
		tvFeed.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
		rlFeed.addView(tvFeed, rlTvFeedParams);		
		
		rlFeed.setBackgroundResource(R.drawable.about_us_item_background_tablet);
		
		TableRow.LayoutParams FeedParams = new TableRow.LayoutParams
				(width + 2 * mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), height);
		FeedParams.setMargins(0,0, mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_aboutapp_margin), 0);
		rlFeed.setLayoutParams(FeedParams);
		rlFeed.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				EasyTracker.getTracker().sendEvent("關於我們", "點擊", "建議回饋", (long)0);
				Uri uri = Uri.parse("mailto:jumplives@gmail.com");  
				String[] ccs={"abooyaya@gmail.com, raywu07@gmail.com, supermfb@gmail.com, form.follow.fish@gmail.com"};
				Intent it = new Intent(Intent.ACTION_SENDTO, uri);
				it.putExtra(Intent.EXTRA_CC, ccs); 
				it.putExtra(Intent.EXTRA_SUBJECT, "[電影窩] 建議回饋"); 
				startActivity(it);  
			}			
		});
		Schedule_row.addView(rlFeed);
		
		
		TextView tvSubmit = new TextView(mFragmentActivity);
		ImageView ivSubmit= new ImageView(mFragmentActivity);
		RelativeLayout rlSubmit = new RelativeLayout(mFragmentActivity);		
			
		ivSubmit.setScaleType(ImageView.ScaleType.CENTER_CROP);
		ivSubmit.setImageResource(R.drawable.declare);
		rlSubmit.addView(ivSubmit, rlIvParams);
		
        RelativeLayout.LayoutParams rlTvSubmitParams = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rlTvSubmitParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlTvSubmitParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlTvSubmitParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
        tvSubmit.setText(mFragmentActivity.getResources().getString(R.string.reader_submit));
        tvSubmit.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
        tvSubmit.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
        rlSubmit.addView(tvSubmit, rlTvSubmitParams);		
		
        rlSubmit.setBackgroundResource(R.drawable.about_us_item_background_tablet);
        TableRow.LayoutParams SubmitParams = new TableRow.LayoutParams
				(width + 2 * mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), height);
        SubmitParams.setMargins(0,0, mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_aboutapp_margin), 0);
        rlSubmit.setLayoutParams(SubmitParams);
        rlSubmit.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		EasyTracker.getTracker().sendEvent("關於我們", "點擊", "作家投稿", (long)0);
				Uri uri = Uri.parse("mailto:jumplives@gmail.com");  
				String[] ccs={"abooyaya@gmail.com, raywu07@gmail.com, supermfb@gmail.com, form.follow.fish@gmail.com"};
				Intent it = new Intent(Intent.ACTION_SENDTO, uri);
				it.putExtra(Intent.EXTRA_CC, ccs); 
				it.putExtra(Intent.EXTRA_SUBJECT, "[電影窩] 作家投稿"); 
				startActivity(it);  
			}				
		});
		Schedule_row.addView(rlSubmit);
		
		
		
		TextView tvFacebook = new TextView(mFragmentActivity);
		ImageView ivFacebook = new ImageView(mFragmentActivity);
		RelativeLayout rlFacebook = new RelativeLayout(mFragmentActivity);		
			
		ivFacebook.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivFacebook.setImageResource(R.drawable.facebook);
        rlFacebook.addView(ivFacebook, rlIvParams);
		
        RelativeLayout.LayoutParams rlTvFacebookParams = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rlTvFacebookParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlTvFacebookParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlTvFacebookParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
		tvFacebook.setText(mFragmentActivity.getResources().getString(R.string.facebook));
		tvFacebook.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
		tvFacebook.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
		rlFacebook.addView(tvFacebook, rlTvFacebookParams);		
		
		rlFacebook.setBackgroundResource(R.drawable.about_us_item_background_tablet);
		TableRow.LayoutParams FacebookParams = new TableRow.LayoutParams
				(width + 2 * mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), height);
		rlFacebook.setLayoutParams(FacebookParams);
		rlFacebook.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				EasyTracker.getTracker().sendEvent("關於我們", "點擊", "FB粉絲團", (long)0);
				Uri uri = Uri.parse("http://www.facebook.com/movietalked");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
			}			
		});
		Schedule_row.addView(rlFacebook);
		

		
		
		TextView tvClear = new TextView(mFragmentActivity);
		ImageView ivClear = new ImageView(mFragmentActivity);
		RelativeLayout rlClear = new RelativeLayout(mFragmentActivity);		
			
		ivClear.setScaleType(ImageView.ScaleType.CENTER_CROP);
		ivClear.setImageResource(R.drawable.delete);
		rlClear.addView(ivClear, rlIvParams);
		
        RelativeLayout.LayoutParams rlTvClearParams = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rlTvClearParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlTvClearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlTvClearParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
		tvClear.setText(mFragmentActivity.getResources().getString(R.string.clear));
		tvClear.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
		tvClear.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
		rlClear.addView(tvClear, rlTvClearParams);		
		
		rlClear.setBackgroundResource(R.drawable.about_us_item_background_tablet);
		TableRow.LayoutParams ClearParams = new TableRow.LayoutParams
				(width + 2 * mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), height);
		rlClear.setLayoutParams(ClearParams);
		rlClear.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				imageLoader.clearMemoryCache();
				imageLoader.clearDiscCache();
				Toast toast = Toast.makeText(mFragmentActivity, 
	            		mFragmentActivity.getResources().getString(R.string.clear_finish), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
			}			
		});
		Schedule_row.addView(rlClear);
		
		
		Schedule_row.setLayoutParams(new LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		llAboutUsOther.addView(Schedule_row);
	}
	
	private String fetchData() {
		NewsAPI api = new NewsAPI();
		appProject = api.getAppProjectList();
		return "progress end";
	}
	
	private void setView(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer
				((int)mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_bg_radius)))
		.build();
		
		for(int i=0; i<appProject.size(); i+=columnNum){
			TableRow Schedule_row = new TableRow(mFragmentActivity);
			for(int j=0; j<columnNum; j++){
				int index = i + j;
				
				TextView tv = new TextView(mFragmentActivity);
				ImageView iv = new ImageView(mFragmentActivity);
				RelativeLayout rl = new RelativeLayout(mFragmentActivity);
				
				DisplayMetrics displayMetrics = new DisplayMetrics();
				mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
				int width = (int) ((displayMetrics.widthPixels * 13 / 20
						- mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_aboutapp_margin) * 5.0f)
						/ 4 - mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin) * 2.0f);
				int height = width + 3 * mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin) 
						+ mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title);
		        
				if(index < appProject.size()) {
					
					iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
			        RelativeLayout.LayoutParams rlIvParams = new RelativeLayout.LayoutParams(width, width);
			        rlIvParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			        rlIvParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			        rlIvParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			        rlIvParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
					rl.addView(iv, rlIvParams);
			        iv.getLayoutParams().width = width;
			        iv.getLayoutParams().height = width;
			        imageLoader.displayImage(appProject.get(index).getIconUrl(), iv, options);
					
					RelativeLayout.LayoutParams rlTvParams = new RelativeLayout.LayoutParams
							(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					rlTvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					rlTvParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
					rlTvParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
							0, 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
					tv.setText(appProject.get(index).getName());
					tv.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
					tv.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
					rl.addView(tv, rlTvParams);
				} else
					rl.setVisibility(View.INVISIBLE);
				
				rl.setBackgroundResource(R.drawable.about_us_item_background_tablet);
				
				TableRow.LayoutParams Params = new TableRow.LayoutParams
						(width + 2 * mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), height);
				if(index % columnNum == 0)
					Params.setMargins(0, 0, 0, 0);
				else
					Params.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_aboutapp_margin), 0, 0, 0);
		        
				rl.setLayoutParams(Params);
				rl.setOnClickListener(new ItemButtonClick(index));
				Schedule_row.addView(rl);
			}
			Schedule_row.setLayoutParams(new LayoutParams
					(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			llAboutUsOtherProductor.addView(Schedule_row);
		}        
	}
	
	class ItemButtonClick implements OnClickListener {
		private int position;
		
		ItemButtonClick(int pos) {
			position = pos;
		}

		public void onClick(View v) {
			PackageManager pm = mFragmentActivity.getPackageManager();
		    Intent appStartIntent = pm.getLaunchIntentForPackage(appProject.get(position).getPack());
		    if(null != appStartIntent) {
		    	EasyTracker.getTracker().sendEvent("關於我們", "開啟App", appProject.get(position).getName(), (long)0);
		    	appStartIntent.addCategory("android.intent.category.LAUNCHER");
		    	appStartIntent.setComponent(new ComponentName(appProject.get(position).getPack(),
		    			appProject.get(position).getClas()));
		    	appStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	mFragmentActivity.startActivity(appStartIntent);
		    } 
		    else {
		    	EasyTracker.getTracker().sendEvent("關於我們", "下載App", appProject.get(position).getName(), (long)0);
		    	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("market://details?id=" + appProject.get(position).getPack())));
		    }
		}
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		pbInit.setVisibility(View.VISIBLE);
    		super.onPreExecute();
        }  
          
        @Override  
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            return fetchData();  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	pbInit.setVisibility(View.GONE);
        	if(appProject != null){
        		setView();		
    		}

	        super.onPostExecute(result);  
        }
    }
	
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance().activityStart(mFragmentActivity); // Add this method.
		EasyTracker.getTracker().sendView("平板關於我們Fragment");
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(mFragmentActivity); // Add this method
	}
}
