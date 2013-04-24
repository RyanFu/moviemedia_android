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
import com.hodo.HodoADView;
import com.hodo.listener.HodoADListener;
import com.jumplife.movienews.NewsContentTabletActivity;
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
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class FeatureTabletFragment extends Fragment implements AdWhirlInterface{	
	
	private View fragmentView;
	private ImageButton imageButtonRefresh;
	private LinearLayout llFeature;
	
	//private ArrayList<Picture> pictures;
	private ArrayList<News> news;
	
	private LoadPictureTask loadPictureTask;
	private ProgressBar pbInit;
	
	private FragmentActivity mFragmentActivity;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_feature, container, false);		
		initView();
		
		loadPictureTask = new LoadPictureTask();
	    if(Build.VERSION.SDK_INT < 11)
	    	loadPictureTask.execute();
        else
        	loadPictureTask.executeOnExecutor(LoadPictureTask.THREAD_POOL_EXECUTOR, 0);
	    
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
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_feature);
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		llFeature = (LinearLayout)fragmentView.findViewById(R.id.ll_feature);
		adListLayout = (LinearLayout) fragmentView.findViewById(R.id.ad_list_layout);
		adLayout = (RelativeLayout)fragmentView.findViewById(R.id.ad_layout);
		adLayout2 = (RelativeLayout)fragmentView.findViewById(R.id.ad_layout2);
		adLayout3 = (RelativeLayout)fragmentView.findViewById(R.id.ad_layout3);
		
		imageButtonRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadPictureTask = new LoadPictureTask();
        	    if(Build.VERSION.SDK_INT < 11)
        	    	loadPictureTask.execute();
                else
                	loadPictureTask.executeOnExecutor(LoadPictureTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
	}	
	
	private void fetchPictureData() {
		NewsAPI api = new NewsAPI();
		news = api.getEditorSelectedList();
	}

	@SuppressWarnings("deprecation")
	private void setPictureView() {
		LayoutInflater myInflater = LayoutInflater.from(mFragmentActivity);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		if(news.size() > 0) {
			//TableRow Schedule_row = new TableRow(getActivity());
			View converView = myInflater.inflate(R.layout.poster_viewpage_item, null);
			TextView tv = (TextView)converView.findViewById(R.id.pager_context);
			ImageView iv = (ImageView)converView.findViewById(R.id.pager_poster);
				
			DisplayMetrics displayMetrics = new DisplayMetrics();
			((Activity) mFragmentActivity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	        int screenWidth = displayMetrics.widthPixels * 3 / 4 - 
	        		mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width) * 2;
	        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
	        iv.getLayoutParams().height = (int)(screenWidth / 2);
	        iv.getLayoutParams().width = screenWidth;
		        
				
			tv.setText(news.get(0).getName());
			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
					(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			tv.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board));
			tv.setLayoutParams(rlParams);
			imageLoader.displayImage(news.get(0).getPosterUrl(), iv, options);
			converView.setId(0);

			converView.setOnClickListener(new OnClickListener(){
				@SuppressLint("InlinedApi")
				@Override
				public void onClick(View arg0) {
					int index = arg0.getId();
					
					EasyTracker.getTracker().sendEvent("編輯精選", "點擊", "新聞id: " +  news.get(index).getId(), (long) news.get(index).getId());
					
					if(news.get(index).getCategory().getTypeId() == 1) {
						Intent newAct = new Intent();
						newAct.setClass(mFragmentActivity, NewsContentTabletActivity.class );
					
			            Bundle bundle = new Bundle();
	
			            bundle.putInt("newsId", news.get(index).getId());
			            bundle.putString("categoryName", news.get(index).getCategory().getName());
			            bundle.putString("releaseDateStr", NewsAPI.dateToString(news.get(index).getReleaseDate()));
			            bundle.putString("origin", news.get(index).getOrigin());
			            bundle.putString("name", news.get(index).getName());
			            
			            newAct.putExtras(bundle);
			            startActivity(newAct);
					} else {
						Dialog dialog = new Dialog(mFragmentActivity, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
						dialog.setContentView(R.layout.dialog_picture);
						TextView tvName = (TextView)dialog.findViewById(R.id.news_name);
						TextView tvOrigin = (TextView)dialog.findViewById(R.id.news_comment);
						ImageView ivPicture = (ImageView)dialog.findViewById(R.id.iv_picture);
						LinearLayout llShare = (LinearLayout)dialog.findViewById(R.id.ll_share);
						
						tvName.setText(news.get(index).getName());
						if(news.get(index).getOrigin() != null && !news.get(index).getOrigin().contains("null"))
							tvOrigin.setText(news.get(index).getOrigin());
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
						imageLoader.displayImage(news.get(index).show(), ivPicture, options);
						
						llShare.setOnClickListener(new ItemButtonClick(index, news.get(index).getShareLink()));
						
						dialog.show();
					} 
				}						
			});
			
			TableRow.LayoutParams Params = new TableRow.LayoutParams
					(screenWidth, screenWidth / 2, 1.0f);
			Params.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
					mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
					0);

			converView.setBackgroundResource(R.drawable.item_background);
			converView.setLayoutParams(Params);
			llFeature.addView(converView);
		}
			

		for(int i=1; i<news.size(); i+=2){
			TableRow Schedule_row = new TableRow(mFragmentActivity);
			for(int j=0; j<2; j++){
				int index = i + j;
				View converView = myInflater.inflate(R.layout.poster_viewpage_item, null);
				
				TextView tv = (TextView)converView.findViewById(R.id.pager_context);
				ImageView iv = (ImageView)converView.findViewById(R.id.pager_poster);
				
				DisplayMetrics displayMetrics = new DisplayMetrics();
				((Activity) mFragmentActivity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
				int screenWidth = displayMetrics.widthPixels * 3 / 8;
		        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		        iv.getLayoutParams().height = (int)(screenWidth / 2);
		        iv.getLayoutParams().width = screenWidth;
		        

				if(index < news.size()) {
					tv.setText(news.get(index).getName());
					tv.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_comment_small));

					RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
							(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					tv.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl_small), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_item_tv_padding_rl_small), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board));
					tv.setLayoutParams(rlParams);
					imageLoader.displayImage(news.get(index).getPosterUrl(), iv, options);
					converView.setId(index);

					converView.setOnClickListener(new OnClickListener(){
						@SuppressLint("InlinedApi")
						@Override
						public void onClick(View arg0) {
							int index = arg0.getId();
							
							EasyTracker.getTracker().sendEvent("編輯精選", "點擊", "新聞id: " +  news.get(index).getId(), (long) news.get(index).getId());
							
							if(news.get(index).getCategory().getTypeId() == 1) {
								Intent newAct = new Intent();
								newAct.setClass(mFragmentActivity, NewsContentTabletActivity.class );
							
					            Bundle bundle = new Bundle();
			
					            bundle.putInt("newsId", news.get(index).getId());
					            bundle.putString("categoryName", news.get(index).getCategory().getName());
					            bundle.putString("releaseDateStr", NewsAPI.dateToString(news.get(index).getReleaseDate()));
					            bundle.putString("origin", news.get(index).getOrigin());
					            bundle.putString("name", news.get(index).getName());
					            
					            newAct.putExtras(bundle);
					            startActivity(newAct);
							} else {
								Dialog dialog = new Dialog(mFragmentActivity, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
								dialog.setContentView(R.layout.dialog_picture);
								TextView tvName = (TextView)dialog.findViewById(R.id.news_name);
								TextView tvOrigin = (TextView)dialog.findViewById(R.id.news_comment);
								ImageView ivPicture = (ImageView)dialog.findViewById(R.id.iv_picture);
								LinearLayout llShare = (LinearLayout)dialog.findViewById(R.id.ll_share);
								
								tvName.setText(news.get(index).getName());
								if(news.get(index).getOrigin() != null && !news.get(index).getOrigin().contains("null"))
									tvOrigin.setText(news.get(index).getOrigin());
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
								imageLoader.displayImage(news.get(index).show(), ivPicture, options);
								
								llShare.setOnClickListener(new ItemButtonClick(index, news.get(index).getShareLink()));
								
								dialog.show();
							} 
						}						
					});
				} else {
					tv.setVisibility(View.INVISIBLE);
				}
				
				TableRow.LayoutParams Params = new TableRow.LayoutParams
						(screenWidth, screenWidth / 2, 0.5f);
				if(index%2 != 0)
					Params.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width)/2, 
							0);
				else
					Params.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width)/2, 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
							mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.overview_category_interval_width), 
							0);
				converView.setLayoutParams(Params);
				converView.setBackgroundResource(R.drawable.item_background);
				Schedule_row.addView(converView);
			}
			Schedule_row.setLayoutParams(new LayoutParams
					(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			llFeature.addView(Schedule_row);
		}
	}
	
	class LoadPictureTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		pbInit.setVisibility(View.VISIBLE);
    		imageButtonRefresh.setVisibility(View.GONE);
    		super.onPreExecute();  
        }  
          
        @Override  
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	fetchPictureData();
            return "progress end";  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	pbInit.setVisibility(View.GONE);
        	if(news != null && news.size() > 0){

        		setPictureView();                
        		imageButtonRefresh.setVisibility(View.GONE);
        	} else {         
        		imageButtonRefresh.setVisibility(View.VISIBLE);
        	}
        	super.onPostExecute(result);
        }
    }
	public void onStart() {
	  super.onStart();
	  // The rest of your onStart() code.
	  EasyTracker.getInstance().activityStart(mFragmentActivity); // Add this method.
	  EasyTracker.getTracker().sendView("平板電影新聞種類列表Fragment");
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
    	
        adWhirlLayout.setGravity(Gravity.CENTER_HORIZONTAL);
	 	
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
	
	@Override
	public void adWhirlGeneric() {
		// TODO Auto-generated method stub
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
            		EasyTracker.getTracker().sendEvent("圖片新聞", "分享", "news id: " + news.get(position).getId(), (long)news.get(position).getId());
            		Toast toast = Toast.makeText(mFragmentActivity, 
            				mFragmentActivity.getResources().getString(R.string.fb_share_success), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
            	}
            }
        });
        Bundle params = request.getParameters();
        params.putString("message", news.get(position).getName());
		request.executeAsync();
	}
}
