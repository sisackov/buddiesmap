package com.buddiesmap.fbhandlers;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.buddiesmap.MainActivity;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfoCallBack implements GraphRequest.Callback {
    private MainActivity mainActivity;
    private UserInfo mLoggedUser;

    public UserInfoCallBack(MainActivity activity) {
        mLoggedUser = new UserInfo();
        mainActivity = activity;
    }

    public void onCompleted(GraphResponse response) {
        /* handle the result */
        Log.d("User Info: ", response.toString());
        JSONObject jsonObject;
        try {
            JSONObject responseObject = response.getJSONObject();
            if (responseObject.has("name")) {
                mLoggedUser.setUserName(responseObject.getString("name"));
            }
            if (responseObject.has("hometown")) {
                jsonObject = responseObject.getJSONObject("hometown");
                mLoggedUser.setUserHometown(jsonObject.getString("name"));
            }
            if (responseObject.has("location")) {
                jsonObject = responseObject.getJSONObject("location");
                mLoggedUser.setUserLocation(jsonObject.getString("name"));
            }
        } catch (JSONException e) {
            String ex = e.getMessage() == null ? "JSONException in UserInfoCallBack" : e.getMessage();
            Log.e(getClass().getCanonicalName(), ex);
        }

        sendBackIntent();
    }

    private void sendBackIntent() {
        Intent rtReturn = new Intent(MainActivity.LOGGED_USER_INFO);
        rtReturn.putExtra(MainActivity.LOGGED_USER_INFO, mLoggedUser);
        LocalBroadcastManager.getInstance(mainActivity).sendBroadcast(rtReturn);
    }
}