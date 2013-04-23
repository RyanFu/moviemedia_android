package com.jumplife.tabletfragment;

import java.util.ArrayList;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.analytics.tracking.android.EasyTracker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.jumplife.adapter.PictureGridAdapter;
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
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class PicturesTabletFragment extends Fragment {	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private PullToRefreshGridView picturesGridView;
	private PictureGridAdapter pictureGridAdapter;
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
	
	public static PicturesTabletFragment NewInstance(int categoryId, String categoryName, int typeId) {
		PicturesTabletFragment fragment = new PicturesTabletFragment();
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
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		picturesGridView = (PullToRefreshGridView)fragmentView.findViewById(R.id.gv_pictures);
		
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
		NewsAPI api = new NewsAPI();
		
		int categoryId = getArguments().getInt("categoryId");
		int typeId = getArguments().getInt("typeId");
		
		newsList = api.getNewsList(categoryId, typeId, page);
		
		return "progress end";
	}
	
	
	private void setListener() {
		picturesGridView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Dialog dialog = new Dialog(mFragmentActivity);
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
				
				EasyTracker.getTracker().sendEvent("圖片新聞", "點擊", "news id: " + newsList.get(arg2).getId(), (long)newsList.get(arg2).getId());
				
				imageLoader.displayImage(newsList.get(arg2).show(), ivPicture, options);
				
				dialog.show();
			}			
		});
		
		picturesGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {
			 @SuppressWarnings("deprecation")
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				 picturesGridView.setLastUpdatedLabel(DateUtils.formatDateTime(mFragmentActivity.getApplicationContext(),
						 System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));
				 RefreshTask task = new RefreshTask();
				if(Build.VERSION.SDK_INT < 11)
					task.execute();
				else
			    	task.executeOnExecutor(RefreshTask.THREAD_POOL_EXECUTOR, 0);
			}
		
			@SuppressWarnings("deprecation")
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				picturesGridView.setLastUpdatedLabel(DateUtils.formatDateTime(mFragmentActivity.getApplicationContext(),
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
		pictureGridAdapter = new PictureGridAdapter(mFragmentActivity, newsList);
		picturesGridView.setAdapter(pictureGridAdapter);
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		picturesGridView.setVisibility(View.GONE);
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
            	picturesGridView.setVisibility(View.VISIBLE);
            	imageButtonRefresh.setVisibility(View.GONE);		
    		} else {
    			picturesGridView.setVisibility(View.GONE);
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
            	picturesGridView.setVisibility(View.VISIBLE);
    			imageButtonRefresh.setVisibility(View.GONE);		
    		} else {
    			picturesGridView.setVisibility(View.GONE);
                imageButtonRefresh.setVisibility(View.VISIBLE);
    		}
        	picturesGridView.onRefreshComplete();        	
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
				pictureGridAdapter.notifyDataSetChanged();
				page += 1;
        	}
			picturesGridView.onRefreshComplete();
            
			super.onPostExecute(result);
        }
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance().activityStart(this.getActivity()); // Add this method.
		EasyTracker.getTracker().sendView("平板圖片列表Fragment");
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this.getActivity()); // Add this method
	}
}
