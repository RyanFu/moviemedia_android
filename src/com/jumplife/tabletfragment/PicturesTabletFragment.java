package com.jumplife.tabletfragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlLayout.ViewAdRunnable;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.analytics.tracking.android.EasyTracker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.hodo.HodoADView;
import com.hodo.listener.HodoADListener;
import com.jumplife.adapter.PictureGridAdapter;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PicturesTabletFragment extends Fragment implements AdWhirlInterface{	
	
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
    
    //for ad
  	LinearLayout adListLayout;
  	RelativeLayout adLayout;
  	RelativeLayout adLayout2;
  	RelativeLayout adLayout3;
  	
  	private AdWhirlLayout adWhirlLayout;
  	private AdWhirlLayout adWhirlLayout2;
  	private AdWhirlLayout adWhirlLayout3;

	private Session session = Session.getActiveSession();	
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private PostRecordTask postRecordTask;

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
	    
	    AdTask adTask = new AdTask();
		adTask.execute();
	    
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
		adLayout = (RelativeLayout)fragmentView.findViewById(R.id.ad_layout);
		adLayout2 = (RelativeLayout)fragmentView.findViewById(R.id.ad_layout2);
		adLayout3 = (RelativeLayout)fragmentView.findViewById(R.id.ad_layout3);
		
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
			@SuppressLint("InlinedApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Dialog dialog = new Dialog(mFragmentActivity, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
				dialog.setContentView(R.layout.dialog_picture);
				TextView tvName = (TextView)dialog.findViewById(R.id.news_name);
				TextView tvOrigin = (TextView)dialog.findViewById(R.id.news_comment);
				ImageView ivPicture = (ImageView)dialog.findViewById(R.id.iv_picture);
				LinearLayout llShare = (LinearLayout)dialog.findViewById(R.id.ll_share);
				
				tvName.setText(newsList.get(arg2).getName());
				if(newsList.get(arg2).getOrigin() != null && !newsList.get(arg2).getOrigin().contains("null"))
					tvOrigin.setText(newsList.get(arg2).getOrigin());
				else
					tvOrigin.setVisibility(View.GONE);
				
				ImageLoader imageLoader = ImageLoader.getInstance();
				DisplayImageOptions  options = new DisplayImageOptions.Builder()
				.cacheInMemory()
				.cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer())
				.build();
				
				RelativeLayout.LayoutParams ivrlParams = new RelativeLayout.LayoutParams
						(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				ivPicture.setLayoutParams(ivrlParams);
				ivPicture.setScaleType(ScaleType.FIT_CENTER);
				imageLoader.displayImage(newsList.get(arg2).show(), ivPicture, options);
				
				llShare.setOnClickListener(new ItemButtonClick(arg2, newsList.get(arg2).getShareLink()));
				
				EasyTracker.getTracker().sendEvent("圖片新聞", "點擊", "news id: " + newsList.get(arg2).getId(), (long)newsList.get(arg2).getId());
				
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
		EasyTracker.getInstance().activityStart(mFragmentActivity); // Add this method.
		EasyTracker.getTracker().sendView("平板圖片列表Fragment");
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(mFragmentActivity); // Add this method
	}
	
	public void setAd() {
    	
    	Resources res = mFragmentActivity.getResources();
    	String adwhirlKey = res.getString(R.string.adwhirl_tablet_key);
    	AdWhirlManager.setConfigExpireTimeout(1000 * 30); 

        AdWhirlTargeting.setTestMode(false);
   		
        adWhirlLayout = new AdWhirlLayout(mFragmentActivity, adwhirlKey);
        adWhirlLayout2 = new AdWhirlLayout(mFragmentActivity, adwhirlKey);	
        adWhirlLayout3 = new AdWhirlLayout(mFragmentActivity, adwhirlKey);	
        
        adWhirlLayout.setAdWhirlInterface(this);
        adWhirlLayout2.setAdWhirlInterface(this);
        adWhirlLayout3.setAdWhirlInterface(this);
        
        adWhirlLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        adWhirlLayout2.setGravity(Gravity.CENTER_HORIZONTAL);
        adWhirlLayout3.setGravity(Gravity.CENTER_HORIZONTAL);
        
    	adLayout.addView(adWhirlLayout);
    	adLayout2.addView(adWhirlLayout2);
    	adLayout3.addView(adWhirlLayout3);
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
	
	class ItemButtonClick implements OnClickListener {
		private int position;
		private String picUrl;

		ItemButtonClick(int pos, String url) {
			position = pos;
			picUrl = url;
		}

		public void onClick(View v) {
			if (session != null && session.isOpened()) {
	            postRecordTask = new PostRecordTask(position, picUrl);
	            postRecordTask.execute();
			} else {
		    	LoginFragment splashFragment = new LoginFragment();
		    	splashFragment.show(mFragmentActivity.getSupportFragmentManager(), "dialog"); 
		    }
		}
	}
	
	public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	class PostRecordTask extends AsyncTask<Integer, Integer, String> {

        private ProgressDialog progressdialogInit;
        private final OnCancelListener cancelListener = new OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
            	PostRecordTask.this.cancel(true);
            }
        };
        
        private int position;
		private String picUrl;
		private Bitmap bitmap;
        
        PostRecordTask(int pos, String url) {
			position = pos;
			picUrl = url;        	
        }
        
        @Override
        protected void onPreExecute() {
        	progressdialogInit = new ProgressDialog(mFragmentActivity);
            progressdialogInit.setTitle(mFragmentActivity.getResources().getString(R.string.fb_share));
            progressdialogInit.setMessage(mFragmentActivity.getResources().getString(R.string.sharing));
            progressdialogInit.setOnCancelListener(cancelListener);
            progressdialogInit.setCanceledOnTouchOutside(false);
        	progressdialogInit.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
        	bitmap = getBitmapFromURL(picUrl);
        	if(bitmap != null)
        		return "progress end";
        	else
        		return "progress fail";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
        	if(result.contains("progress end"))
				publishFeedDialog(position, bitmap);
			else {
				closeProgressDilog();
	            Toast toast = Toast.makeText(mFragmentActivity, 
	            		mFragmentActivity.getResources().getString(R.string.fb_share_failed_again), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Log.d(null, "Facebook share failed");
			}				
        	
        	super.onPostExecute(result);
        }
        
        public void closeProgressDilog() {
        	if(mFragmentActivity != null && !mFragmentActivity.isFinishing() 
        			&& progressdialogInit != null && progressdialogInit.isShowing())
        		progressdialogInit.dismiss();
        }
    }
	
	private void publishFeedDialog(int position, Bitmap bitmap) {
		
		if (hasPublishPermission()) {
			PublishPhotoToFB(bitmap, position);
			return;
        } else {
        	NewPermissionCallBack callback = new NewPermissionCallBack(position, bitmap);
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(mFragmentActivity, PERMISSIONS)
        			.setDefaultAudience(SessionDefaultAudience.EVERYONE);
            session.addCallback(callback);
        	session.requestNewPublishPermissions(newPermissionsRequest);
        	return;
        }
         
    }
	
	class NewPermissionCallBack implements Session.StatusCallback {
		private Bitmap bitmap;
		private int position;
		
		public NewPermissionCallBack(int position, Bitmap bitmap) {
			this.bitmap = bitmap;
			this.position = position;
		}
		
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			Log.d(null, "enter call back");
			PublishPhotoToFB(bitmap, position);
		}		
	};
	
	private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }
	
	public void PublishPhotoToFB(Bitmap bitmap, final int position) {
		Log.d(null, "enter publish fb");
		Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), bitmap, new Request.Callback() {
            public void onCompleted(Response response) {
            	postRecordTask.closeProgressDilog();
                if(response.getError() != null) {
            		Log.d("", "error : " + response.getError().getErrorMessage());
	            	Toast toast = Toast.makeText(mFragmentActivity, 
	            			mFragmentActivity.getResources().getString(R.string.fb_share_failed_again), Toast.LENGTH_LONG);
	                toast.setGravity(Gravity.CENTER, 0, 0);
	                toast.show();
            	} else {
            		EasyTracker.getTracker().sendEvent("圖片新聞", "分享", "news id: " + newsList.get(position).getId(), (long)newsList.get(position).getId());
            		Toast toast = Toast.makeText(mFragmentActivity, 
            				mFragmentActivity.getResources().getString(R.string.fb_share_success), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
            	}
            }
        });
        Bundle params = request.getParameters();
        if(newsList.get(position).getOrigin() != null && !newsList.get(position).getOrigin().equalsIgnoreCase("null")
        		&& !newsList.get(position).getOrigin().replace(" ", "").equals(""))
            params.putString("message", newsList.get(position).getName() + "--《" + newsList.get(position).getOrigin() + "》");
        else
        	params.putString("message", newsList.get(position).getName());
		request.executeAsync();
	}

	@Override
	public void adWhirlGeneric() {
		// TODO Auto-generated method stub
		
	}
}
