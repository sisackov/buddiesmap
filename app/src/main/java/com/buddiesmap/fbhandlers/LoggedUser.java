package com.buddiesmap.fbhandlers;

import java.util.ArrayList;

public class LoggedUser {
    private static LoggedUser mInstance = null;
    private UserInfo mUserInfo = null;
    private ArrayList<String> mUserFriends;

    private LoggedUser() {

    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public ArrayList<String> getUserFriends() {
        return mUserFriends;
    }

    public void setUserFriends(ArrayList<String> userFriends) {
        mUserFriends = userFriends;
    }

    public static LoggedUser getInstance() {
        if (mInstance == null) {
            synchronized (LoggedUser.class) {
                if (mInstance == null) {
                    mInstance = new LoggedUser();
                }
            }
        }
        return mInstance;
    }


    public void destroy() {
        mInstance = null;
        mUserInfo = null;
        mUserFriends = null;
    }
}
