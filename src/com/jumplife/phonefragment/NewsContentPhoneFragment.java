package com.jumplife.phonefragment;

import java.util.Date;


import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlLayout.ViewAdRunnable;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.hodo.HodoADView;
import com.hodo.listener.HodoADListener;
import com.jumplife.adapter.VideosViewPagerAdapter;
import com.jumplife.adapter.WrapSlidingDrawer;
import com.jumplife.movienews.AboutUsActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.NewsCategory;
import com.jumplife.movienews.entity.TextNews;
import com.jumplife.titlebarwebview.TitleBarWebView;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.jumplife.movienews.asynctask.NewsShareTask;
import com.jumplife.phonefragment.NewsPhoneFragment.AdTask;

import android.annotation.SuppressLint;
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
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Toast;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class NewsContentPhoneFragment extends Fragment implements AdWhirlInterface{	
	
	private View fragmentView;
	private View overheadView;
	private TextView topbar_text;
	private TextView textviewTitle;
	private TextView textviewSource;
	private TextView textviewReleaseDate;
	private TitleBarWebView webview;
	private ImageButton imageButtonRefresh;
	private ImageButton imageButtonShare;
	private ImageButton imageButtonAbourUs;
	private VideosViewPagerAdapter mAdapter;
	private RelativeLayout rlViewpager;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	private WrapSlidingDrawer slidingDrawer;
	private ImageView slidingDrawerHandler;
	private ProgressBar pbInit;
	
	private TextNews news;
	
	private LoadDataTask loadtask;
	
	private UiLifecycleHelper uiHelper;
    private StatusCallback callback = new StatusCallback() {
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private FragmentActivity mFragmentActivity;
    
    //for ad
  	RelativeLayout adLayout;
  	private AdWhirlLayout adWhirlLayout;

    @Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
    
    public static NewsContentPhoneFragment NewInstance(int newsId, String categoryName, String releaseDateStr, String origin, String name) {
		NewsContentPhoneFragment fragment = new NewsContentPhoneFragment();
	    Bundle args = new Bundle();	    
	    args.putInt("newsId", newsId);
	    args.putString("categoryName", categoryName);
	    args.putString("releaseDateStr", releaseDateStr);
	    args.putString("origin", origin);
	    args.putString("name", name);
	    
	    fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_news_content, container, false);		
		
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
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(mFragmentActivity, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        uiHelper.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    /**
     * Notifies that the session token has been updated.
     */
    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {

    }
    
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private void initView() {
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_news_content);
		topbar_text = (TextView)fragmentView.findViewById(R.id.topbar_text);
		textviewTitle = (TextView)fragmentView.findViewById(R.id.tv_title);
		textviewSource = (TextView)fragmentView.findViewById(R.id.tv_source);
		textviewReleaseDate = (TextView)fragmentView.findViewById(R.id.tv_date);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		imageButtonShare = (ImageButton)fragmentView.findViewById(R.id.ib_share);
		imageButtonAbourUs = (ImageButton)fragmentView.findViewById(R.id.ib_about_us);
		webview = (TitleBarWebView)fragmentView.findViewById(R.id.webview_pic);
		overheadView = (View)fragmentView.findViewById(R.id.view_overhead);
		adLayout = (RelativeLayout)fragmentView.findViewById(R.id.ad_layout);
		

		topbar_text.setText(getArguments().getString("categoryName"));

		slidingDrawer = (WrapSlidingDrawer)fragmentView.findViewById(R.id.sd_video);
		slidingDrawerHandler = (ImageView)fragmentView.findViewById(R.id.iv_handle);
		rlViewpager = (RelativeLayout)fragmentView.findViewById(R.id.rl_viewpager);		
		mPager = (ViewPager)fragmentView.findViewById(R.id.pager);
		mIndicator = (CirclePageIndicator)fragmentView.findViewById(R.id.indicator);
		
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(true);
		if(Build.VERSION.SDK_INT > 10)
			webview.getSettings().setDisplayZoomControls(false);
		
		imageButtonRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadtask = new LoadDataTask();
                if(Build.VERSION.SDK_INT < 11)
                	loadtask.execute();
                else
                	loadtask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
		
		imageButtonAbourUs.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	Intent newAct = new Intent();
				newAct.setClass(mFragmentActivity, AboutUsActivity.class );
	            startActivity(newAct);
            }
        });
		
		textviewTitle.setText(getArguments().getString("name"));
		textviewSource.setText(getArguments().getString("origin"));
		textviewReleaseDate.setText(NewsAPI.dateToString(NewsAPI.stringToDate(getArguments().getString("releaseDateStr"))));
	}
	
	private String fetchData() {
		NewsAPI api = new NewsAPI();		
		int newsId = getArguments().getInt("newsId");
		String origin = getArguments().getString("origin");
		String name = getArguments().getString("name");
		Date releaseDate = NewsAPI.stringToDate(getArguments().getString("releaseDateStr"));
		//(int id, String name, String posterUrl, String iconUrl, int typeId)
		NewsCategory category = new NewsCategory(-1, getArguments().getString("categoryName"), "", "", -1);
		
		news = api.getTextNews(newsId);
		
		if (news == null) {
			//error handling
		}
		else {
			news.setCategory(category);
			news.setOrigin(origin);
			news.setName(name);
			news.setReleaseDate(releaseDate);
		}
		return "progress end";
	}
	

	private void setView(){
		final String mimeType = "text/html";
        final String encoding = "UTF-8";

		webview.loadDataWithBaseURL("", news.getContent(), mimeType, encoding, "");
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
        mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        LinearLayout.LayoutParams rlLayout = new LinearLayout.LayoutParams(screenWidth, 
        		screenWidth / 2 + mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.title) * 3);
        
        rlViewpager.setLayoutParams(rlLayout);
        
        mAdapter = new VideosViewPagerAdapter(mFragmentActivity, news.getVideoList());

		mPager.setAdapter(mAdapter);
        
		mIndicator.setViewPager(mPager);
        
        slidingDrawer.getLayoutParams().height = displayMetrics.heightPixels / 2;
        slidingDrawer.setVisibility(View.VISIBLE);
        slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener(){
			@Override
			public void onDrawerOpened() {
				overheadView.setVisibility(View.VISIBLE);
				slidingDrawerHandler.setImageResource(R.drawable.handler_down);
			}        	
        });
        slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener(){
			@Override
			public void onDrawerClosed() {
				overheadView.setVisibility(View.INVISIBLE);
				slidingDrawerHandler.setImageResource(R.drawable.handler_up);
			}        	
        });
        
        imageButtonShare.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				publishFeedDialog();
			}        	
        });
	}
	
	private void publishFeedDialog() {
		Session session = Session.getActiveSession();
		
		if (session != null && session.isOpened()) {
	        Bundle params = new Bundle();
	        params.putString("name", news.getName());
	        params.putString("caption", news.getName());
	        params.putString("link", news.getSourceUrl());
	        //params.putString("picture", newsContent.getPosterUrl());
	        
	        // Invoke the dialog
	    	WebDialog feedDialog = ( new WebDialog.FeedDialogBuilder(mFragmentActivity, session, params))
				.setOnCompleteListener(new OnCompleteListener() {
		
					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
			                // and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								int newsId = getArguments().getInt("newsId"); //for log
								EasyTracker.getTracker().sendEvent("文字新聞", "分享", "news id: " + newsId, (long) newsId);
								
								NewsShareTask newsShareTask = new NewsShareTask(newsId);
								newsShareTask.execute();
								
								Toast.makeText(mFragmentActivity,
										mFragmentActivity.getResources().getString(R.string.fb_share_success),
										Toast.LENGTH_SHORT).show();
							} else {
								// User clicked the Cancel button
								Toast.makeText(mFragmentActivity.getApplicationContext(), 
										mFragmentActivity.getResources().getString(R.string.fb_share_cancel), 
		                                Toast.LENGTH_SHORT).show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(mFragmentActivity.getApplicationContext(), 
									mFragmentActivity.getResources().getString(R.string.fb_share_cancel), 
		                            Toast.LENGTH_SHORT).show();
						} else {
							// Generic, ex: network error
							Toast.makeText(mFragmentActivity.getApplicationContext(), 
									mFragmentActivity.getResources().getString(R.string.fb_share_failed), 
		                            Toast.LENGTH_SHORT).show();
						}
					}
					
				})
				.build();
	    	feedDialog.show();
	    } else {
	    	LoginFragment splashFragment = new LoginFragment();
	    	splashFragment.show(mFragmentActivity.getSupportFragmentManager(), "dialog"); 
	    }
    }
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		pbInit.setVisibility(View.VISIBLE);
    		imageButtonRefresh.setVisibility(View.GONE);
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
        	if(news != null){
        		setView();
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
		EasyTracker.getTracker().sendView("手機文字新聞Fragment");
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
