package com.jumplife.movienews;

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
        String featureName = bundle.getString("featureName");          
        FragmentManager fragMgr = this.getSupportFragmentManager();
        NewsContentPhoneFragment newsFragment = NewsContentPhoneFragment.NewInstance(newsId, featureName);
        FragmentTransaction fragTrans = fragMgr.beginTransaction();
        fragTrans.add(android.R.id.content, newsFragment);
        fragTrans.commitAllowingStateLoss();
    }
}
