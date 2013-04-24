package com.jumplife.tabletfragment;

import java.util.ArrayList;


import com.google.analytics.tracking.android.EasyTracker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.jumplife.adapter.NewsGridAdapter;
import com.jumplife.movienews.NewsContentTabletActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class NewsTabletFragment extends Fragment {	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private PullToRefreshGridView newsGridView;
	private NewsGridAdapter newsGridAdapter;
	private ProgressBar pbInit;
	
	//private ArrayList<TextNews> newsContents;
	private ArrayList<News> news;
	
	private LoadDataTask loadtask;
	
	private int page = 1;
    
    private FragmentActivity mFragmentActivity;

    @Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
	
	public static NewsTabletFragment NewInstance(int categoryId, String categoryName, int typeId) {
		NewsTabletFragment fragment = new NewsTabletFragment();
	    Bundle args = new Bundle();
	    args.putInt("categoryId", categoryId);
	    args.putString("categoryName", categoryName);
	    args.putInt("typeId", typeId);
	    fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_news, container, false);		
		
		initView();
        loadtask = new LoadDataTask();
	    if(Build.VERSION.SDK_INT < 11)
	    	loadtask.execute();
        else
        	loadtask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
	    
		return fragmentView;
	}
	
	private void initView() {
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_news);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		newsGridView = (PullToRefreshGridView)fragmentView.findViewById(R.id.gv_news);
		
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
		
		news = api.getNewsList(categoryId, typeId, page);
		
		return "progress end";
	}
	
	private void setListener() {
		imageButtonRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadtask = new LoadDataTask();
                if(Build.VERSION.SDK_INT < 11)
                	loadtask.execute();
                else
                	loadtask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
		
		Log.d(null, "set click item listener ");
		newsGridView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				EasyTracker.getTracker().sendEvent(getArguments().getString("categoryName") + "_新聞列表", "點擊", "new id: " +
		            	news.get(position).getId(), (long)(news.get(position).getId()));
				
				Log.d(null, "click item : " + position);
				Intent newAct = new Intent();
				newAct.setClass(mFragmentActivity, NewsContentTabletActivity.class );
				
				Bundle bundle = new Bundle();				
	            bundle.putInt("newsId", news.get(position).getId());
	            bundle.putString("categoryName", getArguments().getString("categoryName"));	            
	            
	            bundle.putString("releaseDateStr", NewsAPI.dateToString(news.get(position ).getReleaseDate()));	            
	            bundle.putString("origin", news.get(position).getOrigin());
	            bundle.putString("name", news.get(position).getName());
				
	            newAct.putExtras(bundle);
	            startActivity(newAct);
	            
	            Thread mThread = new Thread(new updateNewsWatcheThread(position));
	            mThread.start();
			}
		});
		
		newsGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {
			 @SuppressWarnings("deprecation")
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				 newsGridView.setLastUpdatedLabel(DateUtils.formatDateTime(mFragmentActivity.getApplicationContext(),
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
				newsGridView.setLastUpdatedLabel(DateUtils.formatDateTime(mFragmentActivity.getApplicationContext(),
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
	
	class updateNewsWatcheThread implements Runnable {
		private int position;
		
		updateNewsWatcheThread(int position) {
			this.position = position;
		}
		
		@Override
		public void run() {
			NewsAPI api = new NewsAPI(mFragmentActivity);
			api.updateNewsWatchedWithAccount(news.get(position).getId());
		}		
	}
	
	private void setListAdatper() {
		newsGridAdapter = new NewsGridAdapter(mFragmentActivity, news);

		newsGridView.setAdapter(newsGridAdapter);
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		newsGridView.setVisibility(View.GONE);
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
			if(news != null && news.size() != 0){
        		setListAdatper();
        		setListener();
            	page += 1;
            	newsGridView.setVisibility(View.VISIBLE);
            	imageButtonRefresh.setVisibility(View.GONE);		
    		} else {
    			newsGridView.setVisibility(View.GONE);
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
			if(news != null && news.size() != 0){
        		setListAdatper();
        		setListener();
            	page += 1;
            	newsGridView.setVisibility(View.VISIBLE);
    			imageButtonRefresh.setVisibility(View.GONE);		
    		} else {
    			newsGridView.setVisibility(View.GONE);
                imageButtonRefresh.setVisibility(View.VISIBLE);
    		}
			newsGridView.onRefreshComplete();        	
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
				news.addAll(tmpList);
				newsGridAdapter.notifyDataSetChanged();
				page += 1;
        	}
			newsGridView.onRefreshComplete();
            
			super.onPostExecute(result);
        }
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance().activityStart(this.getActivity()); // Add this method.
		EasyTracker.getTracker().sendView("平板新聞列表Fragment");
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this.getActivity()); // Add this method
	}
}
