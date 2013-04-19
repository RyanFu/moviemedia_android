package com.jumplife.movienews;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.jumplife.phonefragment.PicturesPhoneFragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class PicturesPhoneActivity extends FragmentActivity{
	
	private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getIntent().getExtras();
        int categoryId = bundle.getInt("categoryId", 0);
        String categoryName = bundle.getString("categoryName");
        int typeId = bundle.getInt("typeId", 0);
        
        FragmentManager fragMgr = this.getSupportFragmentManager();
        PicturesPhoneFragment newsFragment = PicturesPhoneFragment.NewInstance(categoryId, categoryName, typeId);
        FragmentTransaction fragTrans = fragMgr.beginTransaction();
        fragTrans.add(android.R.id.content, newsFragment);
        fragTrans.commit();
        
        if(getResources().getBoolean(R.bool.landscape)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
	
	@Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }
    
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        uiHelper.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    /**
     * Notifies that the session token has been updated.
     */
    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {

    }
}
