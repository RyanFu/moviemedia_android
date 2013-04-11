package com.jumplife.phonefragment;

import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.jumplife.adapter.NewsListAdapter;
import com.jumplife.movienews.NewsContentPhoneActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.entity.NewsContent;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class NewsPhoneFragment extends Fragment {	
	
	private View fragmentView;
	private TextView topbar_text;
	private ImageButton imageButtonRefresh;
	private PullToRefreshListView newsListView;
	private NewsListAdapter newsContentListAdapter;
	
	private ArrayList<NewsContent> newsContents;
	
	private LoadDataTask loadtask;
	
	private int page = 1;
	
	public static NewsPhoneFragment NewInstance(int featureId, String featureName) {
		NewsPhoneFragment fragment = new NewsPhoneFragment();
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
		topbar_text = (TextView)fragmentView.findViewById(R.id.topbar_text);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		newsListView = (PullToRefreshListView)fragmentView.findViewById(R.id.lv_news);
		
		topbar_text.setText(getArguments().getString("featureName"));
	}
	
	private String fetchData() {
		newsContents = fakeData();
		
		return "progress end";
	}
	
	private ArrayList<NewsContent> fakeData() {
		ArrayList<NewsContent> tmps = new ArrayList<NewsContent>();
		ArrayList<Video> tmpVideos = new ArrayList<Video>();
		Video tmpVideo = new Video("康熙來囉～～", 
				"https://www.youtube.com/watch?v=U6YOj-zUj1Q", 
				"https://www.youtube.com/watch?v=U6YOj-zUj1Q");
		tmpVideos.add(tmpVideo);
		NewsContent tmp1 = new NewsContent(33, getArguments().getInt("featureId"), 
				"第四屆「金掃帚獎」日前揭曉", "康熙來囉～～", 
				"成為影史票房第二高的中國片 <br /><img src='http://m.udn.com/xhtml/image/7802699-3036999.jpg' alt='' id='test'>" +
				"今年票房破億的電影數量增長不多，具體票房數字卻明顯「豪華」了很多。截至目前，已經有五部電影票房突破2億人民幣大關，" +
				"八部電影衝破1億5000萬人民幣大關。12部過億電影的總票房達到33億7700萬元人民幣，" +
				"這個數字比去年同期八部破億影片累計20億3200萬元人民幣和2011年同期11部破億電影累計17億5500萬元人民幣高了不少 <br />" +
				"僅用兩天時間就突破了億元大關；最慢的是在宣傳方面毫無作為的《神隱任務》——共花了18天時間破億。從整體上看" +
				"，12部電影平均破億時間為6.6天，也就是說單片破億用不了一周。 <br />", 
				"http://pic.pimg.tw/jumplives/1364368222-4123437044.jpg?v=1364368282", "udn", "", tmpVideos);
		tmps.add(tmp1);
		
		return tmps;
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
		newsListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(null, "click item : " + position);
				Intent newAct = new Intent();
				newAct.setClass(getActivity(), NewsContentPhoneActivity.class );
				Bundle bundle = new Bundle();
	            bundle.putInt("newsId", newsContents.get(position - 1).getId());
	            bundle.putString("featureName", getArguments().getString("featureName"));
	            newAct.putExtras(bundle);
	            startActivity(newAct);
			}
		});
		
		newsListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			 @SuppressWarnings("deprecation")
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				 newsListView.setLastUpdatedLabel(DateUtils.formatDateTime(getActivity().getApplicationContext(),
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
				newsListView.setLastUpdatedLabel(DateUtils.formatDateTime(getActivity().getApplicationContext(),
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
		newsContentListAdapter = new NewsListAdapter(getActivity(), newsContents);
		newsListView.setAdapter(newsContentListAdapter);
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
			if(newsContents != null && newsContents.size() != 0){
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
			if(newsContents != null && newsContents.size() != 0){
        		setListAdatper();
        		setListener();
            	page += 1;
        	}
			newsListView.onRefreshComplete();        	
        	super.onPostExecute(result);
        }
	}
	
	class NextPageTask  extends AsyncTask<Integer, Integer, String>{

		private ArrayList<NewsContent> tmpList;
		
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
				newsContents.addAll(tmpList);
				newsContentListAdapter.notifyDataSetChanged();
				page += 1;
        	}
			newsListView.onRefreshComplete();
            
			super.onPostExecute(result);
        }
	}
}
