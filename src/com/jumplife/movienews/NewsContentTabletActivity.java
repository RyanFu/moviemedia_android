package com.jumplife.movienews;

import com.jumplife.tabletfragment.NewsContentTabletFragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class NewsContentTabletActivity extends FragmentActivity{
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
        NewsContentTabletFragment newsFragment = NewsContentTabletFragment.NewInstance(newsId, categoryName, releaseDateStr, origin, name);
        FragmentTransaction fragTrans = fragMgr.beginTransaction();
        fragTrans.add(android.R.id.content, newsFragment);
        fragTrans.commitAllowingStateLoss();
        
        if(getResources().getBoolean(R.bool.tablet)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
