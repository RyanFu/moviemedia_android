package com.jumplife.movienews;

import java.util.HashMap;

import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.movienews.api.NewsAPI;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;
import com.jumplife.tabletfragment.FeatureTabletFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class OverViewActivity extends FragmentActivity{

	private SharePreferenceIO sharepre;	
	private int openCount;
	private int version;
	private LoadPromoteTask loadPromoteTask;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        
        if(getResources().getBoolean(R.bool.tablet)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            
            FeatureTabletFragment features = new FeatureTabletFragment(); 

            FragmentTransaction ft = this.getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.details, features);//将得到的fragment 替换当前的viewGroup内容，add则不替换会依次累加
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);//设置动画效果
            ft.commitAllowingStateLoss();//提交

            TextView topbarText = (TextView)findViewById(R.id.topbar_text);
            topbarText.setText(this.getResources().getString(R.string.app_name));
            
        	ImageButton imageButtonAbourUs = (ImageButton)findViewById(R.id.ib_about_us);
        	imageButtonAbourUs.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                	Intent newAct = new Intent();
    				newAct.setClass(OverViewActivity.this, AboutUsActivity.class );
    	            startActivity(newAct);
                }
            });
        } else {
        	// Promotion
            sharepre = new SharePreferenceIO(OverViewActivity.this);
            openCount = sharepre.SharePreferenceO("opencount", 0);
            version = sharepre.SharePreferenceO("version", 0);
            loadPromoteTask = new LoadPromoteTask();
            Log.d(null, "opencount : " + openCount);
        	if(openCount > 9) {
            	loadPromoteTask.execute();
            }
            openCount += 1;
        	sharepre.SharePreferenceI("opencount", openCount);
        }
    }
	
	@Override
	public void onStart() {
	  super.onStart();
	     // The rest of your onStart() code.
	  EasyTracker.getInstance().activityStart(this); // Add this method.
	  //myTracker.sendView("Home Screen"); // Where myTracker is an instance of Tracker.
	}

	@Override
	public void onStop() {
	  super.onStop();
	  // The rest of your onStop() code.
	  EasyTracker.getInstance().activityStop(this); // Add this method
	}
	
	@Override
	protected void onDestroy(){
        super.onDestroy();
        if (loadPromoteTask!= null && loadPromoteTask.getStatus() != AsyncTask.Status.FINISHED) {
        	loadPromoteTask.closeProgressDilog();
        	loadPromoteTask.cancel(true);
        }
	}
	
	class LoadPromoteTask extends AsyncTask<Integer, Integer, String>{  
        
		private String[] promotion = new String[5];
        private ProgressDialog progressdialogInit;
        private Dialog dialogPromotion;
        
        private OnCancelListener cancelListener = new OnCancelListener(){
		    public void onCancel(DialogInterface arg0){
		    	closeProgressDilog();
		    	LoadPromoteTask.this.cancel(true);
		    }
    	};

    	@Override  
        protected void onPreExecute() {
    		progressdialogInit= new ProgressDialog(OverViewActivity.this);
        	progressdialogInit.setTitle("Load");
        	progressdialogInit.setMessage("Loading…");
        	progressdialogInit.setOnCancelListener(cancelListener);
        	progressdialogInit.setCanceledOnTouchOutside(false);
        	if(progressdialogInit != null && !progressdialogInit.isShowing())
        		progressdialogInit.show();
			super.onPreExecute();  
        }  
    	
		@Override  
        protected String doInBackground(Integer... params) {
			NewsAPI newsAPI = new NewsAPI();
			promotion = newsAPI.getPromotion();
			return "progress end";  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @SuppressLint("InlinedApi")
		@Override  
        protected void onPostExecute(String result) {
        	options = new DisplayImageOptions.Builder()
    		.showStubImage(R.drawable.img_status_loading)
    		.showImageForEmptyUri(R.drawable.img_status_nopicture)
    		.showImageOnFail(R.drawable.img_status_error)
    		.cacheInMemory()
    		.cacheOnDisc()
    		.displayer(new SimpleBitmapDisplayer())
    		.build();
        	
        	closeProgressDilog();
        	
        	if(promotion != null && !promotion[1].equals("null") && Integer.valueOf(promotion[4]) > version) {
        		dialogPromotion = new Dialog(OverViewActivity.this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
				dialogPromotion.setContentView(R.layout.dialog_promotion);
	            ImageView imageView = (ImageView)dialogPromotion.findViewById(R.id.iv_promote);
	            TextView textviewTitle = (TextView)dialogPromotion.findViewById(R.id.tv_promote_title);
	            TextView textviewDescription = (TextView)dialogPromotion.findViewById(R.id.tv_promote_description);
				if(!promotion[0].equals("null"))
					imageLoader.displayImage(promotion[0], imageView, options);
				else
					imageView.setVisibility(View.GONE);
				if(!promotion[2].equals("null"))
					textviewTitle.setText(promotion[2]);
				else
					textviewTitle.setVisibility(View.GONE);
				if(!promotion[3].equals("null"))
					textviewDescription.setText(promotion[3]);
				else
					textviewDescription.setVisibility(View.GONE);
	            dialogPromotion.setOnKeyListener(new OnKeyListener(){
	                public boolean onKey(DialogInterface dialog, int keyCode,
	                        KeyEvent event) {
	                	sharepre.SharePreferenceI("version", Integer.valueOf(promotion[4]));
	                    if(KeyEvent.KEYCODE_BACK==keyCode)
	                    	if(dialogPromotion != null && dialogPromotion.isShowing())
	                    		dialogPromotion.cancel();
	                    return false;
	                }
	            });
	            ((Button)dialogPromotion.findViewById(R.id.bt_promote))
	            .setOnClickListener(
	                new OnClickListener(){
	                    public void onClick(View v) {
	                        //取得文字方塊中的關鍵字字串
	                    	sharepre.SharePreferenceI("version", Integer.valueOf(promotion[4]));
	                    	if(dialogPromotion != null && dialogPromotion.isShowing())
	                    		dialogPromotion.cancel();
	                    	
	                    	HashMap<String, String> parameters = new HashMap<String, String>();
	                    	parameters.put("LINK", promotion[1]);
	    					
	                    	Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(promotion[1]));
	                    	OverViewActivity.this.startActivity(intent);
	                    }
	                }
	            );
	            dialogPromotion.setCanceledOnTouchOutside(false);
	            dialogPromotion.show();
	        	openCount = 0;
        	}
	       	super.onPostExecute(result);  
        } 
        
        public void closeProgressDilog() {
        	if(OverViewActivity.this != null && !OverViewActivity.this.isFinishing() 
        			&& progressdialogInit != null && progressdialogInit.isShowing())
        		progressdialogInit.dismiss();
        }          
    }
}
