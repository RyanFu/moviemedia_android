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
        //setContentView(R.layout.main);
        
        Bundle bundle = this.getIntent().getExtras();
        int newsId = bundle.getInt("categoryId", 0);
        int typeId = bundle.getInt("typeId", 0);
        String featureName = bundle.getString("categoryName");
        
        FragmentManager fragMgr = this.getSupportFragmentManager();
        NewsPhoneFragment newsFragment = NewsPhoneFragment.NewInstance(newsId, featureName, typeId);
        FragmentTransaction fragTrans = fragMgr.beginTransaction();
        fragTrans.add(android.R.id.content, newsFragment);
        fragTrans.commitAllowingStateLoss();
        
        if(getResources().getBoolean(R.bool.landscape)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
