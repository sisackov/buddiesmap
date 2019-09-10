package com.buddiesmap.fbhandlers;

import android.content.Intent;
import android.util.Log;

import com.buddiesmap.MainActivity;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsInfoCallBack implements GraphRequest.Callback {
    private final UserInfo mLoggedUser;
    private final List<String> mFriendslist = new ArrayList<>();

    public FriendsInfoCallBack() {
        mLoggedUser = new UserInfo();
    }

    public void onCompleted(GraphResponse response) {
        /* handle the result */
        Log.d("Friends List: 1", response.toString());
        try {
            JSONObject responseObject = response.getJSONObject();
            JSONArray dataArray = responseObject.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);
                String fbId = dataObject.getString("id");
                String fbName = dataObject.getString("name");
                Log.d("FbId", fbId);
                Log.d("FbName", fbName);
                mFriendslist.add(fbId);
            }
            Log.e("fbfriendList", mFriendslist.toString());
//            List<String> list = mFriendslist;
//            String friends;
//            if (list.size() > 0) {
//                friends = list.toString();
//                if (friends.contains("[")) {
//                    friends = (friends.substring(1, friends.length() - 1));
//                }
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendBackIntent();
    }

    private void sendBackIntent() {
        Intent rtReturn = new Intent(MainActivity.LOGGED_USER_FRIENDS);
        //TODO
//        rtReturn.putExtra("friends", mFriendslist);
//        LocalBroadcastManager.getInstance(null).sendBroadcast(rtReturn);
    }
}
