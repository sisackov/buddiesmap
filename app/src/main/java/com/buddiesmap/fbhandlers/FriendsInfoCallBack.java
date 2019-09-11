package com.buddiesmap.fbhandlers;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.buddiesmap.MainActivity;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.buddiesmap.MainActivity.LOGGED_USER_FRIENDS;

public class FriendsInfoCallBack implements GraphRequest.Callback {
    private MainActivity mainActivity;
    private ArrayList<String> mFriendslist;

    public FriendsInfoCallBack(MainActivity activity) {
        mainActivity = activity;
    }

    public void onCompleted(GraphResponse response) {
        /* handle the result */
        Log.d("Friends List: 1", response.toString());
        try {
            JSONObject responseObject = response.getJSONObject();
            JSONArray dataArray = responseObject.getJSONArray("data");

            mFriendslist = new ArrayList<>(dataArray.length());

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);
                String fbId = dataObject.getString("id");
                mFriendslist.add(fbId);
            }
            Log.d("fbfriendList", mFriendslist.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendBackIntent();
    }

    private void sendBackIntent() {
        Intent rtReturn = new Intent(LOGGED_USER_FRIENDS);
        rtReturn.putStringArrayListExtra(LOGGED_USER_FRIENDS, mFriendslist);
        LocalBroadcastManager.getInstance(mainActivity).sendBroadcast(rtReturn);
    }
}
