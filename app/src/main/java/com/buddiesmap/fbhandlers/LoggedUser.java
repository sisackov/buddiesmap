package com.buddiesmap.fbhandlers;

import java.util.ArrayList;
import java.util.Vector;

public class LoggedUser {
    private static LoggedUser mInstance = null;
    private UserInfo mUserInfo = null;
    private ArrayList<String> mUserFriendIDs;
    private Vector<UserInfo> mUserFriendInfos;

    private LoggedUser() {
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

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public ArrayList<String> getUserFriendIDs() {
        return mUserFriendIDs;
    }

    public void setUserFriendIDs(ArrayList<String> userFriends) {
        mUserFriendIDs = userFriends;
        mUserFriendInfos = new Vector<>(userFriends.size());
    }

    public void addFriendInfo(UserInfo userInfo) {
        mUserFriendInfos.add(userInfo);
    }

    public void destroy() {
        mInstance = null;
        mUserInfo = null;
        mUserFriendIDs = null;
    }
}
