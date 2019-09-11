package com.buddiesmap.fbhandlers;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.buddiesmap.MainActivity;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import static com.buddiesmap.MainActivity.LOGGED_USER_INF;
import static com.buddiesmap.MainActivity.LOGGED_USER_INFO;
import static com.buddiesmap.MainActivity.LOGGED_USER_REQUEST;
import static com.buddiesmap.MainActivity.FRIEND_USER_INFO;

public class UserInfoCallBack implements GraphRequest.Callback {
    private MainActivity mainActivity;
    private Handler mHandler;
    private static int counter = 0;
    private int myCount = 0;

    public UserInfoCallBack(MainActivity activity, Handler mainThreadHandler) {
        mainActivity = activity;
        mHandler = mainThreadHandler;
        myCount = counter++;
    }

    public void onCompleted(GraphResponse response) {
        /* handle the result */
        Log.d("User Info: ", response.toString());
        JSONObject jsonObject;
        UserInfo mUserInfo = new UserInfo();
        try {
            JSONObject responseObject = response.getJSONObject();
            if (responseObject.has("name")) {
                mUserInfo.setUserName(responseObject.getString("name"));
            }
            if (responseObject.has("hometown")) {
                jsonObject = responseObject.getJSONObject("hometown");
                mUserInfo.setUserHometown(jsonObject.getString("name"));
            }
            if (responseObject.has("location")) {
                jsonObject = responseObject.getJSONObject("location");
                mUserInfo.setUserLocation(jsonObject.getString("name"));
            }
        } catch (JSONException e) {
            String ex = e.getMessage() == null ? "JSONException in UserInfoCallBack" : e.getMessage();
            Log.e(getClass().getCanonicalName(), ex);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = response.getRequest().getGraphPath() == LOGGED_USER_REQUEST ?
                        LOGGED_USER_INFO : FRIEND_USER_INFO;
                msg.obj = mUserInfo;
                mHandler.sendMessage(msg);
            }
        }, myCount * 5000);
    }
}