package com.jumplife.phonefragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.widget.LinearLayout.LayoutParams;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlLayout.ViewAdRunnable;
import com.google.analytics.tracking.android.EasyTracker;
import com.hodo.HodoADView;
import com.hodo.listener.HodoADListener;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.AppProject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class AboutUsPhoneFragment extends Fragment implements AdWhirlInterface {	
	
	private View fragmentView;
	private TextView topbar_text;
	private LinearLayout llAboutUs;
	private ProgressBar pbInit;
	private ArrayList<AppProject> appProject;
	
	private LoadDataTask loadtask;

	private FragmentActivity mFragmentActivity;
	
	//for ad
	RelativeLayout adLayout;
	private AdWhirlLayout adWhirlLayout;

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
	    
	    AdTask adTask = new AdTask();
		adTask.execute();
	    
		return fragmentView;
	}
	
	private void initView() {
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_about_us);
		topbar_text = (TextView)fragmentView.findViewById(R.id.topbar_text);
		llAboutUs = (LinearLayout)fragmentView.findViewById(R.id.ll_aboutus);
		adLayout = (RelativeLayout)fragmentView.findViewById(R.id.ad_layout);
        
		topbar_text.setText(mFragmentActivity.getResources().getString(R.string.about_us));
		initBasicView();
	}
	
	private void initBasicView() {
		TableRow Schedule_row_first = new TableRow(mFragmentActivity);
		TableRow Schedule_row_second = new TableRow(mFragmentActivity);
		

        
        DisplayMetrics displayMetrics = new DisplayMetrics();
		mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels / 3;
		TableRow.LayoutParams Params = new TableRow.LayoutParams
				(screenWidth, screenWidth * 5 / 6, 0.33f);				
		mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels / 6;
        RelativeLayout.LayoutParams rlIvParams = new RelativeLayout.LayoutParams(width, width);
        rlIvParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rlIvParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlIvParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
        
        
        
        TextView tvAboutApp = new TextView(mFragmentActivity);
		ImageView ivAboutApp = new ImageView(mFragmentActivity);
		RelativeLayout rlAboutApp = new RelativeLayout(mFragmentActivity);		
			
		ivAboutApp.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivAboutApp.setImageResource(R.drawable.about);
        rlAboutApp.addView(ivAboutApp, rlIvParams);
		
		RelativeLayout.LayoutParams rlTvAboutAppParams = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rlTvAboutAppParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rlTvAboutAppParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rlTvAboutAppParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
		tvAboutApp.setText(mFragmentActivity.getResources().getString(R.string.aboutapp));
		tvAboutApp.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
		tvAboutApp.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
		rlAboutApp.addView(tvAboutApp, rlTvAboutAppParams);		
		
		rlAboutApp.setBackgroundResource(R.drawable.about_us_item_background);		
		rlAboutApp.setLayoutParams(Params);
		rlAboutApp.setOnClickListener(new OnClickListener(){
			@SuppressWarnings("deprecation")
			public void onClick(View arg0) {
				EasyTracker.getTracker().sendEvent("關於我們", "點擊", "關於電影窩", (long)0);
				
				AlertDialog dialog = new AlertDialog.Builder(mFragmentActivity).create();
		        dialog.setTitle(mFragmentActivity.getResources().getString(R.string.aboutapp));
		        dialog.setMessage(mFragmentActivity.getResources().getString(R.string.aboutapp_intro));
		        dialog.setButton(mFragmentActivity.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		                // TODO Auto-generated method stub
		            }
		        });
		        dialog.show();
			}			
		});
		Schedule_row_first.addView(rlAboutApp);
				
		
		
		TextView tvFeed = new TextView(mFragmentActivity);
		ImageView ivFeed = new ImageView(mFragmentActivity);
		RelativeLayout rlFeed = new RelativeLayout(mFragmentActivity);		
			
		ivFeed.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
		
		rlFeed.setBackgroundResource(R.drawable.about_us_item_background);
		
		rlFeed.setLayoutParams(Params);
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
		Schedule_row_first.addView(rlFeed);
		
		
		
		
		TextView tvSubmit = new TextView(mFragmentActivity);
		ImageView ivSubmit= new ImageView(mFragmentActivity);
		RelativeLayout rlSubmit = new RelativeLayout(mFragmentActivity);		
			
		ivSubmit.setScaleType(ImageView.ScaleType.CENTER_CROP);
		ivSubmit.setImageResource(R.drawable.submit);
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
		
        rlSubmit.setBackgroundResource(R.drawable.about_us_item_background);
        rlSubmit.setLayoutParams(Params);
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
        Schedule_row_first.addView(rlSubmit);

		Schedule_row_first.setLayoutParams(new LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		llAboutUs.addView(Schedule_row_first);
		
		
		
		
		TextView tvDeclare = new TextView(mFragmentActivity);
		ImageView ivDeclare = new ImageView(mFragmentActivity);
		RelativeLayout rlDeclare = new RelativeLayout(mFragmentActivity);		
			
		ivDeclare.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivDeclare.setImageResource(R.drawable.declare);
        rlDeclare.addView(ivDeclare, rlIvParams);
		
		RelativeLayout.LayoutParams rlTvDeclareParams = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rlTvDeclareParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rlTvDeclareParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rlTvDeclareParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
		tvDeclare.setText(mFragmentActivity.getResources().getString(R.string.liability_disclaimer));
		tvDeclare.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
		tvDeclare.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
		rlDeclare.addView(tvDeclare, rlTvDeclareParams);		
		
		rlDeclare.setBackgroundResource(R.drawable.about_us_item_background);		
		rlDeclare.setLayoutParams(Params);
		rlDeclare.setOnClickListener(new OnClickListener(){
			@SuppressWarnings("deprecation")
			public void onClick(View arg0) {
				EasyTracker.getTracker().sendEvent("關於我們", "點擊", "免責稱明", (long)0);
				AlertDialog dialog = new AlertDialog.Builder(mFragmentActivity).create();
		        dialog.setTitle(mFragmentActivity.getResources().getString(R.string.liability_disclaimer));
		        dialog.setMessage(Html.fromHtml(mFragmentActivity.getResources().getString(R.string.aboutapp_declare)));
		        dialog.setButton(mFragmentActivity.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		                // TODO Auto-generated method stub
		            }
		        });
		        dialog.show();
			}			
		});
		Schedule_row_second.addView(rlDeclare);
		
		
		
		
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
		
		rlFacebook.setBackgroundResource(R.drawable.about_us_item_background);
		rlFacebook.setLayoutParams(Params);
		rlFacebook.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				EasyTracker.getTracker().sendEvent("關於我們", "點擊", "FB粉絲團", (long)0);
				Uri uri = Uri.parse("http://www.facebook.com/movietalked");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
			}			
		});
		Schedule_row_second.addView(rlFacebook);
		


		RelativeLayout rlempty2 = new RelativeLayout(mFragmentActivity);
		rlempty2.setBackgroundResource(R.drawable.about_us_item_background_normal);
		rlempty2.setLayoutParams(Params);
		//rlempty2.setVisibility(View.INVISIBLE);
		Schedule_row_second.addView(rlempty2);
		
		Schedule_row_second.setLayoutParams(new LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		llAboutUs.addView(Schedule_row_second);
	}
	
	private String fetchData() {
		NewsAPI api = new NewsAPI();
		appProject = api.getAppProjectList();
		return "progress end";
	}
	
	private void setView(){
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		for(int i=0; i<appProject.size(); i+=3){
			TableRow Schedule_row = new TableRow(mFragmentActivity);
			for(int j=0; j<3; j++){
				int index = i + j;
				
				TextView tv = new TextView(mFragmentActivity);
				ImageView iv = new ImageView(mFragmentActivity);
				RelativeLayout rl = new RelativeLayout(mFragmentActivity);
				
				if(index < appProject.size()) {
					
					DisplayMetrics displayMetrics = new DisplayMetrics();
					mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			        int width = displayMetrics.widthPixels / 6;
			        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
			        RelativeLayout.LayoutParams rlIvParams = new RelativeLayout.LayoutParams(width, width);
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
				
				rl.setBackgroundResource(R.drawable.about_us_item_background);
				
				DisplayMetrics displayMetrics = new DisplayMetrics();
				mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		        int screenWidth = displayMetrics.widthPixels / 3;
				TableRow.LayoutParams Params = new TableRow.LayoutParams
						(screenWidth, screenWidth * 5 / 6, 0.33f);				
				rl.setLayoutParams(Params);
				rl.setOnClickListener(new ItemButtonClick(index));
				Schedule_row.addView(rl);
			}
			Schedule_row.setLayoutParams(new LayoutParams
					(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			llAboutUs.addView(Schedule_row);
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
		EasyTracker.getTracker().sendView("手機關於我們Fragment");
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
