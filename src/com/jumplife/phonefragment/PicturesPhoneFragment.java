package com.jumplife.phonefragment;

import java.util.ArrayList;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.analytics.tracking.android.EasyTracker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.jumplife.adapter.PictureListAdapter;
import com.jumplife.movienews.AboutUsActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PicturesPhoneFragment extends Fragment {	
	
	private View fragmentView;
	private TextView topbar_text;
	private ImageButton imageButtonRefresh;
	private ImageButton imageButtonAbourUs;
	private PullToRefreshListView picturesListView;
	private PictureListAdapter pictureListAdapter;
	private ProgressBar pbInit;
	
	//private ArrayList<Picture> pictures;
	private ArrayList<News> newsList;
	
	private LoadDataTask loadtask;
	
	private int page = 1;
    
    private FragmentActivity mFragmentActivity;

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
    
    public static PicturesPhoneFragment NewInstance(int categoryId, String categoryName, int typeId) {
		PicturesPhoneFragment fragment = new PicturesPhoneFragment();
	    Bundle args = new Bundle();
	    args.putInt("categoryId", categoryId);
	    args.putInt("typeId", typeId);
	    args.putString("categoryName", categoryName);
	    fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_picture, container, false);		
		
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
    
	private void initView() {
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_picture);
		topbar_text = (TextView)fragmentView.findViewById(R.id.topbar_text);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		imageButtonAbourUs = (ImageButton)fragmentView.findViewById(R.id.ib_about_us);
		picturesListView = (PullToRefreshListView)fragmentView.findViewById(R.id.listview_pictures);
		
		topbar_text.setText(getArguments().getString("categoryName"));
		
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
		
	}
	
	private String fetchData() {
		NewsAPI api = new NewsAPI();
		
		int categoryId = getArguments().getInt("categoryId");
		int typeId = getArguments().getInt("typeId");
		
		newsList = api.getNewsList(categoryId, typeId, page);
		
		
		return "progress end";
	}
	/*
	private ArrayList<Picture> fakeData() {
		ArrayList<Picture> tmps = new ArrayList<Picture>();
		Picture tmp1 = new Picture(35, 2, "有詐～～", "http://pic.pimg.tw/jumplives/1364379312-2318126965.jpg", "蝙蝠俠");
		Picture tmp2 = new Picture(36, 2, "ＮＯ～～～～～", "http://pic.pimg.tw/jumplives/1364379312-3610758421.jpg?v=1364379375", "");
		tmps.add(tmp1);
		tmps.add(tmp2);
		
		return tmps;
	}
	*/
	
	private void setListener() {
		picturesListView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressLint("InlinedApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Dialog dialog = new Dialog(mFragmentActivity, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
				dialog.setContentView(R.layout.dialog_picture);
				ImageView ivPicture = (ImageView)dialog.findViewById(R.id.iv_picture);
				RelativeLayout.LayoutParams ivrlParams = new RelativeLayout.LayoutParams
						(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				ivPicture.setLayoutParams(ivrlParams);
				ivPicture.setScaleType(ScaleType.FIT_CENTER);
				
				ImageLoader imageLoader = ImageLoader.getInstance();
				DisplayImageOptions  options = new DisplayImageOptions.Builder()
				.cacheInMemory()
				.cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer())
				.build();
				
				EasyTracker.getTracker().sendEvent("圖片新聞", "點擊", "news id: " + newsList.get(arg2-1).getId(), (long)newsList.get(arg2-1).getId());
				
				imageLoader.displayImage(newsList.get(arg2-1).show(), ivPicture, options);
				
				dialog.show();
			}			
		});
		
		picturesListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			 @SuppressWarnings("deprecation")
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				 picturesListView.setLastUpdatedLabel(DateUtils.formatDateTime(mFragmentActivity.getApplicationContext(),
						 System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));
				 RefreshTask task = new RefreshTask();
				if(Build.VERSION.SDK_INT < 11)
					task.execute();
				else
			    	task.executeOnExecutor(RefreshTask.THREAD_POOL_EXECUTOR, 0);
			}
		
			@SuppressWarnings("deprecation")
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				picturesListView.setLastUpdatedLabel(DateUtils.formatDateTime(mFragmentActivity.getApplicationContext(),
							System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
									| DateUtils.FORMAT_ABBREV_ALL));
		    	 NextPageTask task = new NextPageTask();
					if(Build.VERSION.SDK_INT < 11)
						task.execute();
			        else
			        	task.executeOnExecutor(NextPageTask.THREAD_POOL_EXECUTOR, 0);
		     }
		 });
	}
	
	private void setListAdatper() {
		pictureListAdapter = new PictureListAdapter(mFragmentActivity, newsList);

		picturesListView.setAdapter(pictureListAdapter);
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		picturesListView.setVisibility(View.GONE);
        	imageButtonRefresh.setVisibility(View.GONE);
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
        	if(newsList != null && newsList.size() != 0){
        		setListAdatper();
        		setListener();
            	page += 1;
            	picturesListView.setVisibility(View.VISIBLE);
            	imageButtonRefresh.setVisibility(View.GONE);		
    		} else {
            	picturesListView.setVisibility(View.GONE);
                imageButtonRefresh.setVisibility(View.VISIBLE);
    		}

	        super.onPostExecute(result);  
        }
    }
	
	class RefreshTask  extends AsyncTask<Integer, Integer, String>{

		@Override  
        protected void onPreExecute() {
			imageButtonRefresh.setVisibility(View.GONE);
			page = 1;
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
		protected void onPostExecute(String result) {
			if(newsList != null && newsList.size() != 0){
        		setListAdatper();
        		setListener();
            	page += 1;
            	picturesListView.setVisibility(View.VISIBLE);
    			imageButtonRefresh.setVisibility(View.GONE);		
    		} else {
            	picturesListView.setVisibility(View.GONE);
                imageButtonRefresh.setVisibility(View.VISIBLE);
    		}
			picturesListView.onRefreshComplete();        	
        	super.onPostExecute(result);
        }
	}
	
	class NextPageTask  extends AsyncTask<Integer, Integer, String>{

		private ArrayList<News> tmpList;
		
		@Override  
        protected void onPreExecute() {
			super.onPreExecute();  
        }  
        @Override
		protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	int categoryId = getArguments().getInt("categoryId");
    		int typeId = getArguments().getInt("typeId");
    		
    		NewsAPI api = new NewsAPI();    		
    		tmpList = api.getNewsList(categoryId, typeId, page);
        	return "progress end";
		}
		@Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        } 
		protected void onPostExecute(String result) {
			if(tmpList != null && tmpList.size() > 0){
				newsList.addAll(tmpList);
				pictureListAdapter.notifyDataSetChanged();
				page += 1;
        	}
			picturesListView.onRefreshComplete();
            
			super.onPostExecute(result);
        }
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance().activityStart(this.getActivity()); // Add this method.
		EasyTracker.getTracker().sendView("手機圖片列表Fragment");
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this.getActivity()); // Add this method
	}
}
