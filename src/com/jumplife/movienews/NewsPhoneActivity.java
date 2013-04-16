package com.jumplife.movienews;

import com.jumplife.phonefragment.NewsPhoneFragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class NewsPhoneActivity extends FragmentActivity{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle bundle = this.getIntent().getExtras();
        int newsId = bundle.getInt("featureId", 0);
        String featureName = bundle.getString("featureName");        
        FragmentManager fragMgr = this.getSupportFragmentManager();
        NewsPhoneFragment newsFragment = NewsPhoneFragment.NewInstance(newsId, featureName);
        FragmentTransaction fragTrans = fragMgr.beginTransaction();
        fragTrans.add(android.R.id.content, newsFragment);
        fragTrans.commitAllowingStateLoss();
        
        if(getResources().getBoolean(R.bool.tablet)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
