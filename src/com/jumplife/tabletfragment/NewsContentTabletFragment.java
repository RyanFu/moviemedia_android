package com.jumplife.tabletfragment;

import java.util.Date;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.adapter.VideoListAdapter;
import com.jumplife.movienews.AboutUsActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.NewsCategory;
import com.jumplife.movienews.entity.TextNews;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;

public class NewsContentTabletFragment extends Fragment {	
	
	private View fragmentView;
	private TextView topbar_text;
	private TextView textviewTitle;
	private TextView textviewSource;
	private TextView textviewReleaseDate;
	private WebView webview;
	private ImageButton imageButtonRefresh;
	private ImageButton imageButtonShare;
	private ImageButton imageButtonAbourUs;
	private ListView lvVideo;
	private VideoListAdapter videoListAdapter;
	private ProgressBar pbInit;
	
	//private TextNews newsContent;
	private TextNews news;
	
	private LoadDataTask loadtask;
    
    private FragmentActivity mFragmentActivity;

    @Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
	
	private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
	public static NewsContentTabletFragment NewInstance(int newsId, String categoryName, String releaseDateStr, String origin, String name) {
		NewsContentTabletFragment fragment = new NewsContentTabletFragment();
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
    
	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_news_content);
		topbar_text = (TextView)fragmentView.findViewById(R.id.topbar_text);
		textviewTitle = (TextView)fragmentView.findViewById(R.id.tv_title);
		textviewSource = (TextView)fragmentView.findViewById(R.id.tv_source);
		textviewReleaseDate = (TextView)fragmentView.findViewById(R.id.tv_date);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		imageButtonShare = (ImageButton)fragmentView.findViewById(R.id.ib_share);
		imageButtonAbourUs = (ImageButton)fragmentView.findViewById(R.id.ib_about_us);
		webview = (WebView)fragmentView.findViewById(R.id.webview_pic);
		lvVideo = (ListView)fragmentView.findViewById(R.id.listview_video);
		
		topbar_text.setText(getArguments().getString("featureName"));
		
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setDefaultZoom(ZoomDensity.CLOSE);  
		webview.setInitialScale(150);
		
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
				newAct.setClass(getActivity(), AboutUsActivity.class );
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
		news.setReleaseDate(releaseDate);
		news.setCategory(category);
		news.setOrigin(origin);
		news.setName(name);

		return "progress end";
	}
	
	
	private void setView(){
		final String mimeType = "text/html";
        final String encoding = "UTF-8";
        webview.loadDataWithBaseURL("", news.getContent(), mimeType, encoding, "");
		
		imageButtonShare.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				publishFeedDialog();
			}        	
        });
		
		videoListAdapter = new VideoListAdapter(mFragmentActivity, news.getVideoList());
		lvVideo.setAdapter(videoListAdapter);
        
		
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
	        params.putString("name", "Facebook SDK for Android");
	        params.putString("caption", "Build great social apps and get more installs.");
	        params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	        params.putString("link", "https://developers.facebook.com/android");
	        params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
	        
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
								
								Toast.makeText(mFragmentActivity,
										"Posted story, id: "+postId,
										Toast.LENGTH_SHORT).show();
							} else {
								// User clicked the Cancel button
								Toast.makeText(mFragmentActivity.getApplicationContext(), 
		                                "Publish cancelled", 
		                                Toast.LENGTH_SHORT).show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(mFragmentActivity.getApplicationContext(), 
		                            "Publish cancelled", 
		                            Toast.LENGTH_SHORT).show();
						} else {
							// Generic, ex: network error
							Toast.makeText(mFragmentActivity.getApplicationContext(), 
		                            "Error posting story", 
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
		EasyTracker.getInstance().activityStart(this.getActivity()); // Add this method.
		EasyTracker.getTracker().sendView("平板文字新聞Fragment");
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this.getActivity()); // Add this method
	}
}
