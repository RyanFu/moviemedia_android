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
import android.widget.Toast;
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
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
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
				(screenWidth, LayoutParams.MATCH_PARENT, 0.33f);				
		mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels / 6;
        LinearLayout.LayoutParams llIvParams = new LinearLayout.LayoutParams(width, width);
        //rlIvParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        //rlIvParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        llIvParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
        
        
        
        TextView tvAboutApp = new TextView(mFragmentActivity);
		ImageView ivAboutApp = new ImageView(mFragmentActivity);
		LinearLayout llAboutApp = new LinearLayout(mFragmentActivity);		
			
		ivAboutApp.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivAboutApp.setImageResource(R.drawable.about);
        llAboutApp.addView(ivAboutApp, llIvParams);
		
        LinearLayout.LayoutParams llTvAboutAppParams = new LinearLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//rlTvAboutAppParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		//rlTvAboutAppParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		llTvAboutAppParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
		tvAboutApp.setText(mFragmentActivity.getResources().getString(R.string.aboutapp));
		tvAboutApp.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
		tvAboutApp.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
		llAboutApp.addView(tvAboutApp, llTvAboutAppParams);		
		
		llAboutApp.setBackgroundResource(R.drawable.about_us_item_background);
		llAboutApp.setOrientation(LinearLayout.VERTICAL);
		llAboutApp.setGravity(Gravity.CENTER_HORIZONTAL);
		llAboutApp.setLayoutParams(Params);
		llAboutApp.setOnClickListener(new OnClickListener(){
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
		Schedule_row_first.addView(llAboutApp);
				
		
		
		TextView tvFeed = new TextView(mFragmentActivity);
		ImageView ivFeed = new ImageView(mFragmentActivity);
		LinearLayout llFeed = new LinearLayout(mFragmentActivity);		
			
		ivFeed.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivFeed.setImageResource(R.drawable.feedback);
        llFeed.addView(ivFeed, llIvParams);
		
        LinearLayout.LayoutParams llTvFeedParams = new LinearLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//rlTvFeedParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		//rlTvFeedParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		llTvFeedParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
		tvFeed.setText(mFragmentActivity.getResources().getString(R.string.advice_and_feedback));
		tvFeed.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
		tvFeed.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
		llFeed.addView(tvFeed, llTvFeedParams);		
		
		llFeed.setBackgroundResource(R.drawable.about_us_item_background);
		llFeed.setOrientation(LinearLayout.VERTICAL);
		llFeed.setGravity(Gravity.CENTER_HORIZONTAL);
		llFeed.setLayoutParams(Params);
		llFeed.setOnClickListener(new OnClickListener(){
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
		Schedule_row_first.addView(llFeed);
		
		
		
		
		TextView tvSubmit = new TextView(mFragmentActivity);
		ImageView ivSubmit= new ImageView(mFragmentActivity);
		LinearLayout llSubmit = new LinearLayout(mFragmentActivity);		
			
		ivSubmit.setScaleType(ImageView.ScaleType.CENTER_CROP);
		ivSubmit.setImageResource(R.drawable.submit);
		llSubmit.addView(ivSubmit, llIvParams);
		
		LinearLayout.LayoutParams llTvSubmitParams = new LinearLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //rlTvSubmitParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //rlTvSubmitParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        llTvSubmitParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
        tvSubmit.setText(mFragmentActivity.getResources().getString(R.string.reader_submit));
        tvSubmit.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
        tvSubmit.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
        llSubmit.addView(tvSubmit, llTvSubmitParams);		
		
        llSubmit.setBackgroundResource(R.drawable.about_us_item_background);
        llSubmit.setOrientation(LinearLayout.VERTICAL);
        llSubmit.setGravity(Gravity.CENTER_HORIZONTAL);
        llSubmit.setLayoutParams(Params);
        llSubmit.setOnClickListener(new OnClickListener(){
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
        Schedule_row_first.addView(llSubmit);

		Schedule_row_first.setLayoutParams(new LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		llAboutUs.addView(Schedule_row_first);
		
		
		
		
		TextView tvDeclare = new TextView(mFragmentActivity);
		ImageView ivDeclare = new ImageView(mFragmentActivity);
		LinearLayout llDeclare = new LinearLayout(mFragmentActivity);		
			
		ivDeclare.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivDeclare.setImageResource(R.drawable.declare);
        llDeclare.addView(ivDeclare, llIvParams);
		
        LinearLayout.LayoutParams llTvDeclareParams = new LinearLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//rlTvDeclareParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		//rlTvDeclareParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		llTvDeclareParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
		tvDeclare.setText(mFragmentActivity.getResources().getString(R.string.liability_disclaimer));
		tvDeclare.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
		tvDeclare.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
		llDeclare.addView(tvDeclare, llTvDeclareParams);		
		
		llDeclare.setBackgroundResource(R.drawable.about_us_item_background);
		llDeclare.setOrientation(LinearLayout.VERTICAL);
		llDeclare.setGravity(Gravity.CENTER_HORIZONTAL);
		llDeclare.setLayoutParams(Params);
		llDeclare.setOnClickListener(new OnClickListener(){
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
		Schedule_row_second.addView(llDeclare);
		
		
		
		
		TextView tvFacebook = new TextView(mFragmentActivity);
		ImageView ivFacebook = new ImageView(mFragmentActivity);
		LinearLayout llFacebook = new LinearLayout(mFragmentActivity);		
			
		ivFacebook.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivFacebook.setImageResource(R.drawable.facebook);
        llFacebook.addView(ivFacebook, llIvParams);
		
        LinearLayout.LayoutParams llTvFacebookParams = new LinearLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //rlTvFacebookParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //rlTvFacebookParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        llTvFacebookParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
		tvFacebook.setText(mFragmentActivity.getResources().getString(R.string.facebook));
		tvFacebook.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
		tvFacebook.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
		llFacebook.addView(tvFacebook, llTvFacebookParams);		
		
		llFacebook.setBackgroundResource(R.drawable.about_us_item_background);
		llFacebook.setOrientation(LinearLayout.VERTICAL);
		llFacebook.setGravity(Gravity.CENTER_HORIZONTAL);
		llFacebook.setLayoutParams(Params);
		llFacebook.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				EasyTracker.getTracker().sendEvent("關於我們", "點擊", "FB粉絲團", (long)0);
				Uri uri = Uri.parse("http://www.facebook.com/movietalked");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
			}			
		});
		Schedule_row_second.addView(llFacebook);
		


		TextView tvClear = new TextView(mFragmentActivity);
		ImageView ivClear = new ImageView(mFragmentActivity);
		LinearLayout llClear = new LinearLayout(mFragmentActivity);		
			
		ivClear.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivClear.setImageResource(R.drawable.delete);
        llClear.addView(ivClear, llIvParams);
		
        LinearLayout.LayoutParams llTvClearParams = new LinearLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //rlTvClearParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //rlTvClearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        llTvClearParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				0, 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
		tvClear.setText(mFragmentActivity.getResources().getString(R.string.clear));
		tvClear.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
		tvClear.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
		llClear.addView(tvClear, llTvClearParams);		
		
		llClear.setBackgroundResource(R.drawable.about_us_item_background);
		llClear.setOrientation(LinearLayout.VERTICAL);
		llClear.setGravity(Gravity.CENTER_HORIZONTAL);
		llClear.setLayoutParams(Params);
		llClear.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				imageLoader.clearMemoryCache();
				imageLoader.clearDiscCache();
				Toast toast = Toast.makeText(mFragmentActivity, 
	            		mFragmentActivity.getResources().getString(R.string.clear_finish), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
			}			
		});
		Schedule_row_second.addView(llClear);
		
		Schedule_row_second.setLayoutParams(new LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		llAboutUs.addView(Schedule_row_second);
	}
	
	private String fetchData() {
		NewsAPI api = new NewsAPI();
		appProject = api.getAppProjectList(mFragmentActivity);
		return "progress end";
	}
	
	private void setView(){
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
				LinearLayout ll = new LinearLayout(mFragmentActivity);
				
				if(index < appProject.size()) {
					
					DisplayMetrics displayMetrics = new DisplayMetrics();
					mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			        int width = displayMetrics.widthPixels / 6;
			        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
			        LinearLayout.LayoutParams llIvParams = new LinearLayout.LayoutParams(width, width);
			        //rlIvParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			        //rlIvParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			        llIvParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
					ll.addView(iv, llIvParams);
			        iv.getLayoutParams().width = width;
			        iv.getLayoutParams().height = width;
			        imageLoader.displayImage(appProject.get(index).getIconUrl(), iv, options);
					
			        LinearLayout.LayoutParams llTvParams = new LinearLayout.LayoutParams
							(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					//llTvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					//llTvParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
					llTvParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
							0, 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_margin));
					tv.setText(appProject.get(index).getName());
					tv.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.about_us_title));
					tv.setTextColor(mFragmentActivity.getResources().getColor(R.color.about_us_tv));
					ll.addView(tv, llTvParams);
				} else
					ll.setVisibility(View.INVISIBLE);
				
				ll.setBackgroundResource(R.drawable.about_us_item_background);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setGravity(Gravity.CENTER_HORIZONTAL);
				DisplayMetrics displayMetrics = new DisplayMetrics();
				mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		        int screenWidth = displayMetrics.widthPixels / 3;
				TableRow.LayoutParams Params = new TableRow.LayoutParams
						(screenWidth, LayoutParams.MATCH_PARENT, 0.33f);				
				ll.setLayoutParams(Params);
				ll.setOnClickListener(new ItemButtonClick(index));
				Schedule_row.addView(ll);
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
