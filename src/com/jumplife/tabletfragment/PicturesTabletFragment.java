package com.jumplife.tabletfragment;

import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.jumplife.adapter.PictureAdapter;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;
import com.jumplife.movienews.entity.Picture;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;

public class PicturesTabletFragment extends Fragment {	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private PullToRefreshGridView picturesGridView;
	private PictureAdapter pictureGridAdapter;
	
	//private ArrayList<Picture> pictures;
	private ArrayList<News> newsList;
	
	private LoadDataTask loadtask;
	
	private int page = 1;
	
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
	
	private void initView() {
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
		
		newsList = api.getNewsList(categoryId, typeId, 1);
		
		return "progress end";
	}
	
	
	private void setListener() {
		picturesGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}			
		});
		
		picturesGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {
			 @SuppressWarnings("deprecation")
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				 picturesGridView.setLastUpdatedLabel(DateUtils.formatDateTime(getActivity().getApplicationContext(),
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
				picturesGridView.setLastUpdatedLabel(DateUtils.formatDateTime(getActivity().getApplicationContext(),
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
		pictureGridAdapter = new PictureAdapter(getActivity(), newsList);
		picturesGridView.setAdapter(pictureGridAdapter);
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
        	if(newsList != null && newsList.size() != 0){
        		setListAdatper();
        		setListener();
            	page += 1;
            	imageButtonRefresh.setVisibility(View.GONE);		
    		} else
                imageButtonRefresh.setVisibility(View.VISIBLE);

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
        	if(newsList != null && newsList.size() != 0){
        		setListAdatper();
        		setListener();
            	page += 1;
            	imageButtonRefresh.setVisibility(View.GONE);		
    		} else
                imageButtonRefresh.setVisibility(View.VISIBLE);
        	picturesGridView.onRefreshComplete();        	
        	super.onPostExecute(result);
        }
	}
	
	class NextPageTask  extends AsyncTask<Integer, Integer, String>{

		private ArrayList<Picture> tmpList;
		
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
				newsList.addAll(tmpList);
				pictureGridAdapter.notifyDataSetChanged();
				page += 1;
        	}
			picturesGridView.onRefreshComplete();
            
			super.onPostExecute(result);
        }
	}
}
