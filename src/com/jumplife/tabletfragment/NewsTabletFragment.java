package com.jumplife.tabletfragment;

import java.util.ArrayList;


import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.jumplife.adapter.NewsListAdapter;
import com.jumplife.movienews.NewsContentPhoneActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;
import com.jumplife.movienews.entity.TextNews;
import com.jumplife.movienews.entity.Video;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

public class NewsTabletFragment extends Fragment {	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private PullToRefreshGridView newsGridView;
	private NewsListAdapter newsGridAdapter;
	
	//private ArrayList<TextNews> newsContents;
	private ArrayList<News> news;
	
	private LoadDataTask loadtask;
	
	private int page = 1;
	
	public static NewsTabletFragment NewInstance(int featureId, String featureName) {
		NewsTabletFragment fragment = new NewsTabletFragment();
	    Bundle args = new Bundle();
	    args.putInt("featureId", featureId);
	    args.putString("featureName", featureName);
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
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		newsGridView = (PullToRefreshGridView)fragmentView.findViewById(R.id.gv_news);
	}
	
	private String fetchData() {
		NewsAPI api = new NewsAPI();
		
		int categoryId = getArguments().getInt("categoryId");
		int typeId = getArguments().getInt("typeId");
		
		news = api.getNewsList(categoryId, typeId, 1);
		
		return "progress end";
	}
	
	private void setView() {
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
				Log.d(null, "click item : " + position);
				Intent newAct = new Intent();
				newAct.setClass(getActivity(), NewsContentPhoneActivity.class );
				Bundle bundle = new Bundle();
	            bundle.putInt("newsId", news.get(position - 1).getId());
	            bundle.putString("featureName", getArguments().getString("featureName"));
	            newAct.putExtras(bundle);
	            startActivity(newAct);
			}
		});
		
		newsGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {
			 @SuppressWarnings("deprecation")
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				 newsGridView.setLastUpdatedLabel(DateUtils.formatDateTime(getActivity().getApplicationContext(),
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
				newsGridView.setLastUpdatedLabel(DateUtils.formatDateTime(getActivity().getApplicationContext(),
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
		newsGridAdapter = new NewsListAdapter(getActivity(), news);
		newsGridView.setAdapter(newsGridAdapter);
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
        	setView();		
			if(news != null && news.size() != 0){
        		setListAdatper();
        		setListener();
            	page += 1;
        	}

	        super.onPostExecute(result);  
        }
    }
	
	class RefreshTask  extends AsyncTask<Integer, Integer, String>{

		@Override  
        protected void onPreExecute() {
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
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	setView();		
			if(news != null && news.size() != 0){
        		setListAdatper();
        		setListener();
            	page += 1;
        	}
			newsGridView.onRefreshComplete();        	
        	super.onPostExecute(result);
        }
	}
	
	class NextPageTask  extends AsyncTask<Integer, Integer, String>{

		private ArrayList<TextNews> tmpList;
		
		@Override  
        protected void onPreExecute() {
			super.onPreExecute();  
        }  
        @Override
		protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	/*VarietyAPI api = new VarietyAPI();
        	tmpList = api.getVarietyChapter(varietyId, page);*/
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
}
