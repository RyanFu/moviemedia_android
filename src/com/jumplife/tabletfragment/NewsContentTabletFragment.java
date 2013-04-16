package com.jumplife.tabletfragment;

import java.util.ArrayList;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.jumplife.adapter.VideoListAdapter;
import com.jumplife.movienews.R;
import com.jumplife.movienews.entity.NewsContent;
import com.jumplife.movienews.entity.Video;
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
	private ListView lvVideo;
	private VideoListAdapter videoListAdapter;
	
	private NewsContent newsContent;
	
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
    
	public static NewsContentTabletFragment NewInstance(int newsId, String featureName) {
		NewsContentTabletFragment fragment = new NewsContentTabletFragment();
	    Bundle args = new Bundle();
	    args.putInt("newsId", newsId);
	    args.putString("featureName", featureName);
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
		topbar_text = (TextView)fragmentView.findViewById(R.id.topbar_text);
		textviewTitle = (TextView)fragmentView.findViewById(R.id.tv_title);
		textviewSource = (TextView)fragmentView.findViewById(R.id.tv_source);
		textviewReleaseDate = (TextView)fragmentView.findViewById(R.id.tv_date);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		imageButtonShare = (ImageButton)fragmentView.findViewById(R.id.ib_share);
		webview = (WebView)fragmentView.findViewById(R.id.webview_pic);
		lvVideo = (ListView)fragmentView.findViewById(R.id.listview_video);
		
		topbar_text.setText(getArguments().getString("featureName"));
		
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setDefaultZoom(ZoomDensity.CLOSE);  
		webview.setInitialScale(200);
		
		imageButtonRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadtask = new LoadDataTask();
                if(Build.VERSION.SDK_INT < 11)
                	loadtask.execute();
                else
                	loadtask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
	}
	
	private String fetchData() {
		newsContent = fakeData();
		return "progress end";
	}
	
	private NewsContent fakeData() {
		ArrayList<Video> tmpVideos = new ArrayList<Video>();
		Video tmpVideo = new Video("康熙來囉～～", 
				"https://www.youtube.com/watch?v=U6YOj-zUj1Q", 
				"http://img.youtube.com/vi/jpgU6YOj-zUj1Q/0.jpg");
		tmpVideos.add(tmpVideo);
		tmpVideos.add(tmpVideo);
		tmpVideos.add(tmpVideo);
		NewsContent tmp1 = new NewsContent(33, getArguments().getInt("featureId"), 
				"第四屆「金掃帚獎」日前揭曉", "康熙來囉～～", 
				"成為影史票房第二高的中國片 <br />" +
				"今年票房破億的電影數量增長不多，具體票房數字卻明顯「豪華」了很多。截至目前，已經有五部電影票房突破2億人民幣大關，" +
				"八部電影衝破1億5000萬人民幣大關。12部過億電影的總票房達到33億7700萬元人民幣，" +
				"這個數字比去年同期八部破億影片累計20億3200萬元人民幣和2011年同期11部破億電影累計17億5500萬元人民幣高了不少 <br />" +
				"僅用兩天時間就突破了億元大關；最慢的是在宣傳方面毫無作為的《神隱任務》——共花了18天時間破億。從整體上看" +
				"，12部電影平均破億時間為6.6天，也就是說單片破億用不了一周。 <br />", 
				"http://pic.pimg.tw/jumplives/1364368222-4123437044.jpg?v=1364368282", "udn", "", tmpVideos);
		
		return tmp1;
	}
	
	private void setView(){
		textviewTitle.setText(newsContent.getName());
		textviewSource.setText(newsContent.getSource());
		textviewReleaseDate.setText(newsContent.getReleaseDate());
		
		final String mimeType = "text/html";
        final String encoding = "UTF-8";        
        webview.loadDataWithBaseURL("", newsContent.getContent(), mimeType, encoding, "");
		
		imageButtonShare.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				publishFeedDialog();
			}        	
        });
		
		videoListAdapter = new VideoListAdapter(mFragmentActivity, newsContent.getVideos());
		lvVideo.setAdapter(videoListAdapter);
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
        	if(newsContent != null){
        		setView();
        		imageButtonRefresh.setVisibility(View.GONE);		
    		} else
                imageButtonRefresh.setVisibility(View.VISIBLE);

	        super.onPostExecute(result);  
        }
    }
}
