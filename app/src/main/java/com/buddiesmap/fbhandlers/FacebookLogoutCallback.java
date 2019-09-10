package com.buddiesmap.fbhandlers;

import android.util.Log;
import android.widget.Toast;

import com.buddiesmap.MainActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;

public class FacebookLogoutCallback extends AccessTokenTracker {
    private MainActivity mainActivity;

    public FacebookLogoutCallback(MainActivity activity) {
        mainActivity = activity;
    }

    /**
     * The method that will be called with the access token changes.
     *
     * @param oldAccessToken     The access token before the change.
     * @param currentAccessToken The new access token.
     */
    @Override
    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
        if (currentAccessToken == null) {
            Log.d("FacebookLogoutCallback", "onLogout catched");
            mainActivity.onUserLogout();//This is my code
            Toast.makeText(mainActivity, "Logged out", Toast.LENGTH_LONG).show();
        }
    }
}
