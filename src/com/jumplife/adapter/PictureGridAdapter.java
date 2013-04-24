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
import com.jumplife.movienews.R;
import com.jumplife.movienews.asynctask.NewsShareTask;
import com.jumplife.movienews.entity.News;
import com.jumplife.phonefragment.LoginFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PictureGridAdapter extends BaseAdapter {
	private FragmentActivity mActivity;
	//private ArrayList<Picture> pictures;
	private ArrayList<News> news;
	private DisplayImageOptions options;
	private PostRecordTask postRecordTask;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private Session session = Session.getActiveSession();
	private final int numColumns = 2;
	private View[] viewsInRow = new View[numColumns];
	
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	
	public PictureGridAdapter(FragmentActivity mActivity,  ArrayList<News> news){
		this.news = news;
		this.mActivity = mActivity;
	}

	public int getCount() {
		
		return news.size();
	}

	public Object getItem(int position) {

		return news.get(position);
	}

	public long getItemId(int position) {
	
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer
				((int)mActivity.getResources().getDimensionPixelSize(R.dimen.pictures_lv_item_radius)))
		.build();
		
		ViewGroup container = new GridViewItemContainer(mActivity);
		
		LayoutInflater myInflater = LayoutInflater.from(mActivity);
		View converView = myInflater.inflate(R.layout.listview_picture, null);
		
		container.addView(converView);
		converView = container;
		
		ImageView imageviewNewsPhoto = (ImageView)converView.findViewById(R.id.news_poster);		
		TextView textvieTitle = (TextView)converView.findViewById(R.id.news_name);
		TextView textViewContent = (TextView)converView.findViewById(R.id.news_comment);		
		LinearLayout llShare = (LinearLayout)converView.findViewById(R.id.ll_share);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels * 3 / 8;
        imageviewNewsPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageviewNewsPhoto.getLayoutParams().height = screenWidth * 3 / 5;
        imageviewNewsPhoto.getLayoutParams().width = screenWidth;
		imageLoader.displayImage(news.get(position).show(), imageviewNewsPhoto, options);
		
		textvieTitle.setText(news.get(position).getName());
		
		String origin = "";
		if (news.get(position).getOrigin() != null && (!news.get(position).getOrigin().equalsIgnoreCase("null")))
			origin = news.get(position).getOrigin() ;
		
		textViewContent.setText(origin);
		
		llShare.setOnClickListener(new ItemButtonClick(position, news.get(position).getShareLink()));
		
		viewsInRow[position % numColumns] = converView;
        GridViewItemContainer referenceView = (GridViewItemContainer)converView;
        if ((position % numColumns == (numColumns-1)) || (position == getCount()-1)) {
            referenceView.setViewsInRow(viewsInRow);
        }
        else {
            referenceView.setViewsInRow(null);
        }
        
		return converView;

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
		    	splashFragment.show(mActivity.getSupportFragmentManager(), "dialog"); 
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
        	progressdialogInit = new ProgressDialog(mActivity);
            progressdialogInit.setTitle(mActivity.getResources().getString(R.string.fb_share));
            progressdialogInit.setMessage(mActivity.getResources().getString(R.string.sharing));
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
	            Toast toast = Toast.makeText(mActivity, 
	            		mActivity.getResources().getString(R.string.fb_share_failed_again), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Log.d(null, "Facebook share failed");
			}				
        	
        	super.onPostExecute(result);
        }
        
        public void closeProgressDilog() {
        	if(mActivity != null && !mActivity.isFinishing() 
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
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(mActivity, PERMISSIONS)
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
	            	Toast toast = Toast.makeText(mActivity, 
	            			mActivity.getResources().getString(R.string.fb_share_failed_again), Toast.LENGTH_LONG);
	                toast.setGravity(Gravity.CENTER, 0, 0);
	                toast.show();
            	} else {
            		EasyTracker.getTracker().sendEvent("圖片新聞", "分享", "news id: " + news.get(position).getId(), (long)news.get(position).getId());
            		
            		NewsShareTask newsShareTask = new NewsShareTask(news.get(position).getId());
            		newsShareTask.execute();
            		
            		Toast toast = Toast.makeText(mActivity, 
            				mActivity.getResources().getString(R.string.fb_share_success), Toast.LENGTH_LONG);
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
