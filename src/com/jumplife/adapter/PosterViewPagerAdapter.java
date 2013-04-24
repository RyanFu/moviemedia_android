package com.jumplife.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.movienews.NewsContentPhoneActivity;
import com.jumplife.movienews.R;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.movienews.entity.News;
import com.jumplife.phonefragment.LoginFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.viewpagerindicator.IconPagerAdapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class PosterViewPagerAdapter extends PagerAdapter implements IconPagerAdapter{

	private FragmentActivity mFragmentActivity;
	private ArrayList<News> news;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	private Session session = Session.getActiveSession();	
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private PostRecordTask postRecordTask;
	
	public PosterViewPagerAdapter(FragmentActivity mFragmentActivity, ArrayList<News> news) {
		this.mFragmentActivity = mFragmentActivity;
		this.news = news;
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}
	
	@Override
	public int getCount() {
		return news.size();
	}
	
	@Override
	public void destroyItem(View pager, int position, Object view) {
		//((ViewPager) pager).removeView(((ViewPager)pager).getChildAt(position));
		((ViewPager) pager).removeView(pager);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}	
	
	@SuppressWarnings("deprecation")
	@Override
	public Object instantiateItem(View pager, int pos) {
        
		View view = View.inflate(mFragmentActivity, R.layout.poster_viewpage_item, null);
        TextView textViewContext = (TextView)view.findViewById(R.id.pager_context);
        TextView textViewFeature = (TextView)view.findViewById(R.id.pager_type);        
        ImageView imageViewMoviePoster = (ImageView)view.findViewById(R.id.pager_poster);
        
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        imageViewMoviePoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageViewMoviePoster.getLayoutParams().height = (int)(screenWidth / 2);
        imageViewMoviePoster.getLayoutParams().width = screenWidth;
        imageViewMoviePoster.setBackgroundResource(R.drawable.overview_category_item_poster_background);
        
        textViewFeature.setText(news.get(pos).getCategory().getName());
        RelativeLayout.LayoutParams rlFeatureParams = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rlFeatureParams.setMargins(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_margin), 
        		mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_margin), 
        		mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_margin), 
        		mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_margin));
        rlFeatureParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rlFeatureParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		textViewFeature.setGravity(Gravity.CENTER);
		textViewFeature.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_padding), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_padding), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_padding), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.feature_padding));
		textViewFeature.setLayoutParams(rlFeatureParams);
		if(news.get(pos).getCategory().getName() == null || news.get(pos).getCategory().getName().contains(""))
			textViewFeature.setVisibility(View.INVISIBLE);
		else
			textViewFeature.setVisibility(View.GONE);
		
        textViewContext.setText(news.get(pos).getName());
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams
				(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		textViewContext.setGravity(Gravity.CENTER);
		textViewContext.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board), 
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.text_board));
		textViewContext.setLayoutParams(rlParams);
		
		imageLoader.displayImage(news.get(pos).getPosterUrl(), imageViewMoviePoster, options);
        
		view.setOnClickListener(new ItemButtonClick(pos));
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent me) {
				ImageView imageViewMoviePoster = (ImageView)v.findViewById(R.id.pager_poster);
				
				if (me.getAction() == MotionEvent.ACTION_DOWN) {
					imageViewMoviePoster.setColorFilter(Color.argb(150, 0, 0, 0));
	            } else if (me.getAction() == MotionEvent.ACTION_UP) {
	            	imageViewMoviePoster.setColorFilter(Color.argb(0, 0, 0, 0)); 
	            } else if (me.getAction() == MotionEvent.ACTION_CANCEL) {
	            	imageViewMoviePoster.setColorFilter(Color.argb(0, 0, 0, 0)); 
	            }
	            return false;
	        }

	    });
		
        ((ViewPager)pager).addView(view, ((ViewPager)pager).getChildCount() > pos ? pos : ((ViewPager)pager).getChildCount());
        //((ViewPager)pager).addView(view);
        
	    return view;
	}
	
	class ItemButtonClick implements OnClickListener {
		private int position;

		ItemButtonClick(int pos) {
			position = pos;
		}

		@SuppressLint("InlinedApi")
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			Intent newAct = new Intent();
			Bundle bundle = new Bundle();
			
			EasyTracker.getTracker().sendEvent("編輯精選", "點擊", "新聞id: " +  news.get(position).getId(), (long) news.get(position).getId());
			
            if (news.get(position).getCategory().getTypeId() == 1) {
				newAct.setClass(mFragmentActivity, NewsContentPhoneActivity.class );
				bundle.putInt("newsId", news.get(position).getId());
	            bundle.putString("categoryName", news.get(position).getCategory().getName());
	            bundle.putString("releaseDateStr", NewsAPI.dateToString(news.get(position).getReleaseDate()));
	            bundle.putString("origin", "");
	            bundle.putString("name", news.get(position).getName());
	            
	            newAct.putExtras(bundle);
	            mFragmentActivity.startActivity(newAct);
			} else {
				Dialog dialog = new Dialog(mFragmentActivity, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
				dialog.setContentView(R.layout.dialog_picture);
				TextView tvName = (TextView)dialog.findViewById(R.id.news_name);
				TextView tvOrigin = (TextView)dialog.findViewById(R.id.news_comment);
				ImageView ivPicture = (ImageView)dialog.findViewById(R.id.iv_picture);
				LinearLayout llShare = (LinearLayout)dialog.findViewById(R.id.ll_share);
				
				tvName.setText(news.get(position).getName());
				if(news.get(position).getOrigin() != null && !news.get(position).getOrigin().contains("null"))
					tvOrigin.setText(news.get(position).getOrigin());
				else
					tvOrigin.setVisibility(View.GONE);
				
				ImageLoader imageLoader = ImageLoader.getInstance();
				DisplayImageOptions optionsPic = new DisplayImageOptions.Builder()
				.cacheInMemory()
				.cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer())
				.build();
				
				RelativeLayout.LayoutParams ivrlParams = new RelativeLayout.LayoutParams
						(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				ivPicture.setLayoutParams(ivrlParams);
				ivPicture.setScaleType(ScaleType.FIT_CENTER);
				imageLoader.displayImage(news.get(position).show(), ivPicture, optionsPic);
				
				llShare.setOnClickListener(new ButtonClick(position, news.get(position).getShareLink()));
				
				dialog.show();
			}            
		}
	}

	class ButtonClick implements OnClickListener {
		private int position;
		private String picUrl;

		ButtonClick(int pos, String url) {
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
	
	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return index;
	}
}