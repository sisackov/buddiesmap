package com.buddiesmap.fbhandlers;

import java.util.Vector;

public class UserInfo {
    private volatile String userName;
    private volatile String userLocation;
    private volatile String userHometown;
    private Vector<String> userFriends;

    public String getUserName() {
        return userName;
    }

    public synchronized void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public synchronized void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getUserHometown() {
        return userHometown;
    }

    public synchronized void setUserHometown(String userHometown) {
        this.userHometown = userHometown;
    }
}
