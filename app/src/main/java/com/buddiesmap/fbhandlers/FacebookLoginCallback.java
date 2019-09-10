package com.buddiesmap.fbhandlers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.buddiesmap.MainActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

public class FacebookLoginCallback implements FacebookCallback<LoginResult> {
    private MainActivity mainActivity;

    public FacebookLoginCallback(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        mainActivity.setResult(Activity.RESULT_OK);
        Log.d("FacebookLoginCallback", "Login");
        mainActivity.sendGraphRequests();
    }

    @Override
    public void onCancel() {
        mainActivity.setResult(Activity.RESULT_CANCELED);
        Toast.makeText(mainActivity, "Login Cancel", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(FacebookException e) {
        // Handle exception
        Toast.makeText(mainActivity, e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
