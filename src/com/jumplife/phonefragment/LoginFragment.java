/**
 * Copyright 2012 Facebook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jumplife.phonefragment;

//import java.util.Arrays;
//import java.util.List;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.jumplife.movienews.R;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoginFragment extends DialogFragment {
	
    //private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);        
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        LoginButton authButton = (LoginButton) view.findViewById(R.id.login_button);
        authButton.setFragment(this);
        //authButton.setPublishPermissions(PERMISSIONS);
        
        return view;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
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
        Log.d(getTag(), "facebook login onActivityResult");
        
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
    	if (session != null && session.isOpened()) {
        	Log.d(getTag(), "session != null && session.isOpened()");
        	this.dismiss();
        } else
        	Log.d(getTag(), "session == null || !session.isOpened()");
    }
}
