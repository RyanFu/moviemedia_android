package com.jumplife.phonefragment;

import java.util.ArrayList;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlLayout.ViewAdRunnable;
import com.google.analytics.tracking.android.EasyTracker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.hodo.HodoADView;
import com.hodo.listener.HodoADListener;
import com.jumplife.adapter.NewsListAdapter;
import com.jumplife.movienews.AboutUsActivity;
import com.jumplife.movienews.NewsContentPhoneActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewsPhoneFragment extends Fragment implements AdWhirlInterface{	
	
	private View fragmentView;
	private TextView topbar_text;
	private ImageButton imageButtonRefresh;
	private ImageButton imageButtonAbourUs;
	private PullToRefreshListView newsListView;
	private NewsListAdapter newsContentListAdapter;
	private ProgressBar pbInit;
	
	//private ArrayList<TextNews> newsContents;
	private ArrayList<News> news;
	
	private LoadDataTask loadtask;
	
	private int page = 1;
	
	//for ad
	RelativeLayout adLayout;
	private AdWhirlLayout adWhirlLayout;
	private FragmentActivity mFragmentActivity;
	
	public static NewsPhoneFragment NewInstance(int categoryId, String categoryName, int typeId) {
		NewsPhoneFragment fragment = new NewsPhoneFragment();
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
	    
	    AdTask adTask = new AdTask();
		adTask.execute();
	    
		return fragmentView;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
	
	private void initView() {
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_news);
		topbar_text = (TextView)fragmentView.findViewById(R.id.topbar_text);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		imageButtonAbourUs = (ImageButton)fragmentView.findViewById(R.id.ib_about_us);
		newsListView = (PullToRefreshListView)fragmentView.findViewById(R.id.lv_news);
		adLayout = (RelativeLayout)fragmentView.findViewById(R.id.ad_layout);
		
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
				newAct.setClass(mFragmentActivity, AboutUsActivity.class );
	            startActivity(newAct);
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
		newsListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(null, "click item : " + position);
				Intent newAct = new Intent();
				newAct.setClass(mFragmentActivity, NewsContentPhoneActivity.class );
				Bundle bundle = new Bundle();
				
	            bundle.putInt("newsId", news.get(position - 1).getId());
	            bundle.putString("categoryName", getArguments().getString("categoryName"));
	            
	            bundle.putString("releaseDateStr", NewsAPI.dateToString(news.get(position - 1).getReleaseDate()));	            
	            bundle.putString("origin", news.get(position - 1).getOrigin());
	            bundle.putString("name", news.get(position - 1).getName());
	            
	            EasyTracker.getTracker().sendEvent(getArguments().getString("categoryName") + "新聞列表", "點擊", "new id: " +
	            	news.get(position - 1).getId(), (long)(news.get(position - 1).getId()));
	            
	            newAct.putExtras(bundle);
	            startActivity(newAct);

	            Thread mThread = new Thread(new updateNewsWatcheThread(position - 1));
	            mThread.start();
			}
		});
		
		newsListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			 @SuppressWarnings("deprecation")
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				 newsListView.setLastUpdatedLabel(DateUtils.formatDateTime(mFragmentActivity.getApplicationContext(),
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
				newsListView.setLastUpdatedLabel(DateUtils.formatDateTime(mFragmentActivity.getApplicationContext(),
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
		newsContentListAdapter = new NewsListAdapter(mFragmentActivity, news);
		newsListView.setAdapter(newsContentListAdapter);
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		newsListView.setVisibility(View.GONE);
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
            	newsListView.setVisibility(View.VISIBLE);
            	imageButtonRefresh.setVisibility(View.GONE);		
    		} else {
    			newsListView.setVisibility(View.GONE);
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
            	newsListView.setVisibility(View.VISIBLE);
    			imageButtonRefresh.setVisibility(View.GONE);		
    		} else {
    			newsListView.setVisibility(View.GONE);
                imageButtonRefresh.setVisibility(View.VISIBLE);
    		}
			newsListView.onRefreshComplete();        	
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
				newsContentListAdapter.notifyDataSetChanged();
				page += 1;
        	}
			newsListView.onRefreshComplete();
            
			super.onPostExecute(result);
        }
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance().activityStart(this.mFragmentActivity); // Add this method.
		EasyTracker.getTracker().sendView("手機新聞列表Fragment");
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this.mFragmentActivity); // Add this method
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
