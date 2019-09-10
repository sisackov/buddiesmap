package com.buddiesmap.fbhandlers;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class UserInfo implements Parcelable {
    private String userName;
    private String userLocation;
    private String userHometown;

    UserInfo() {
    }

    private UserInfo(@NotNull Parcel in) {
        userName = in.readString();
        userLocation = in.readString();
        userHometown = in.readString();
    }

    public String getUserName() {
        return userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLocation() {
        return userLocation;
    }

    void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getUserHometown() {
        return userHometown;
    }

    void setUserHometown(String userHometown) {
        this.userHometown = userHometown;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(userName);
        dest.writeString(userLocation);
        dest.writeString(userHometown);
    }
}
