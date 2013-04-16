package com.jumplife.movienews;

import com.jumplife.tabletfragment.FeatureTabletFragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class OverViewActivity extends FragmentActivity{
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
        }
    }
}
