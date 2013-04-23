package com.jumplife.movienews;

import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.phonefragment.NewsContentPhoneFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class NewsContentPhoneActivity extends FragmentActivity{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle bundle = this.getIntent().getExtras();
        
        int newsId = bundle.getInt("newsId", 0);
        String categoryName = bundle.getString("categoryName");
        String releaseDateStr = bundle.getString("releaseDateStr");
        String origin = bundle.getString("origin");
        String name = bundle.getString("name");
        
        FragmentManager fragMgr = this.getSupportFragmentManager();
        NewsContentPhoneFragment newsFragment = NewsContentPhoneFragment.NewInstance(newsId, categoryName, releaseDateStr, origin, name);
        FragmentTransaction fragTrans = fragMgr.beginTransaction();
        fragTrans.add(android.R.id.content, newsFragment);
        fragTrans.commitAllowingStateLoss();
    }
	
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance().activityStart(this); // Add this method.
	}
	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this); // Add this method
	}
}
