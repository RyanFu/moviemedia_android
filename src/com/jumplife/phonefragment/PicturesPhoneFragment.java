package com.jumplife.phonefragment;

import java.util.ArrayList;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.jumplife.adapter.PictureAdapter;
import com.jumplife.movienews.R;
import com.jumplife.movienews.entity.Picture;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class PicturesPhoneFragment extends Fragment {	
	
	private View fragmentView;
	private TextView topbar_text;
	private ImageButton imageButtonRefresh;
	private PullToRefreshListView picturesListView;
	private PictureAdapter pictureListAdapter;
	
	private ArrayList<Picture> pictures;
	
	private LoadDataTask loadtask;
	
	private int page = 1;
	
	private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
	public static PicturesPhoneFragment NewInstance(int featureId, String featureName) {
		PicturesPhoneFragment fragment = new PicturesPhoneFragment();
	    Bundle args = new Bundle();
	    args.putInt("featureId", featureId);
	    args.putString("featureName", featureName);
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
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
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
		topbar_text = (TextView)fragmentView.findViewById(R.id.topbar_text);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		picturesListView = (PullToRefreshListView)fragmentView.findViewById(R.id.listview_pictures);
		
		topbar_text.setText(getArguments().getString("featureName"));
		
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
		pictures = fakeData();
		
		return "progress end";
	}
	
	private ArrayList<Picture> fakeData() {
		ArrayList<Picture> tmps = new ArrayList<Picture>();
		Picture tmp1 = new Picture(35, 2, "有詐～～", "http://pic.pimg.tw/jumplives/1364379312-2318126965.jpg", "蝙蝠俠");
		Picture tmp2 = new Picture(36, 2, "ＮＯ～～～～～", "http://pic.pimg.tw/jumplives/1364379312-3610758421.jpg?v=1364379375", "");
		tmps.add(tmp1);
		tmps.add(tmp2);
		
		return tmps;
	}
	
	private void setListener() {
		picturesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}			
		});
		
		picturesListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			 @SuppressWarnings("deprecation")
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				 picturesListView.setLastUpdatedLabel(DateUtils.formatDateTime(getActivity().getApplicationContext(),
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
				picturesListView.setLastUpdatedLabel(DateUtils.formatDateTime(getActivity().getApplicationContext(),
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
		pictureListAdapter = new PictureAdapter(getActivity(), pictures);
		picturesListView.setAdapter(pictureListAdapter);
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
        	if(pictures != null && pictures.size() != 0){
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
        	if(pictures != null && pictures.size() != 0){
        		setListAdatper();
        		setListener();
            	page += 1;
            	imageButtonRefresh.setVisibility(View.GONE);		
    		} else
                imageButtonRefresh.setVisibility(View.VISIBLE);
			picturesListView.onRefreshComplete();        	
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
				pictures.addAll(tmpList);
				pictureListAdapter.notifyDataSetChanged();
				page += 1;
        	}
			picturesListView.onRefreshComplete();
            
			super.onPostExecute(result);
        }
	}
}
