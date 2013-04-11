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
import com.jumplife.movienews.R;
import com.jumplife.movienews.entity.Picture;
import com.jumplife.phonefragment.LoginFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

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

public class PictureAdapter extends BaseAdapter {
	private FragmentActivity mActivity;
	private ArrayList<Picture> pictures;
	private DisplayImageOptions options;
	private PostRecordTask postRecordTask;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private Session session = Session.getActiveSession();
	
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	
	public PictureAdapter(FragmentActivity mActivity,  ArrayList<Picture> pictures){
		this.pictures = pictures;
		this.mActivity = mActivity;
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.img_status_loading)
		.showImageForEmptyUri(R.drawable.img_status_nopicture)
		.showImageOnFail(R.drawable.img_status_error)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}

	public int getCount() {
		
		return pictures.size();
	}

	public Object getItem(int position) {

		return pictures.get(position);
	}

	public long getItemId(int position) {
	
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater myInflater = LayoutInflater.from(mActivity);
		View converView = myInflater.inflate(R.layout.listview_picture, null);
		
		ImageView imageviewNewsPhoto = (ImageView)converView.findViewById(R.id.news_poster);
		
		TextView textvieTitle = (TextView)converView.findViewById(R.id.news_name);
		TextView textViewContent = (TextView)converView.findViewById(R.id.news_comment);
		
		LinearLayout llShare = (LinearLayout)converView.findViewById(R.id.ll_share);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        imageviewNewsPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageviewNewsPhoto.getLayoutParams().height = screenWidth * 3 / 5;
        imageviewNewsPhoto.getLayoutParams().width = screenWidth;
		imageLoader.displayImage(pictures.get(position).getPicUrl(), imageviewNewsPhoto, options);
		
		textvieTitle.setText(pictures.get(position).getContent());
		textViewContent.setText(pictures.get(position).getSource());
		
		llShare.setOnClickListener(new ItemButtonClick(position, pictures.get(position).getPicUrl()));
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
            progressdialogInit.setTitle("Facebook分享");
            progressdialogInit.setMessage("分享中…");
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
	            Toast toast = Toast.makeText(mActivity, "Facebook分享失敗 請再分享一次", Toast.LENGTH_LONG);
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
        	Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), bitmap, new Request.Callback() {
                public void onCompleted(Response response) {
                	postRecordTask.closeProgressDilog();
                    if(response.getError() != null) {
                		Log.d("", "error : " + response.getError().getErrorMessage());
		            	Toast toast = Toast.makeText(mActivity, "Facebook分享失敗 請再分享一次", Toast.LENGTH_LONG);
		                toast.setGravity(Gravity.CENTER, 0, 0);
		                toast.show();
                	} else {
                		Toast toast = Toast.makeText(mActivity, "Facebook分享成功", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                	}
                }
            });
            Bundle params = request.getParameters();
			params.putString("message", pictures.get(position).getContent());
			request.executeAsync();
			return;
        } else {
        	postRecordTask.closeProgressDilog();
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(mActivity, PERMISSIONS)
        			.setDefaultAudience(SessionDefaultAudience.EVERYONE);
        	session.requestNewPublishPermissions(newPermissionsRequest);
        	return;
        }
         
    }
	
	private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }
}
