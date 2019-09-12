package com.buddiesmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.buddiesmap.fbhandlers.FacebookLoginCallback;
import com.buddiesmap.fbhandlers.FacebookLogoutCallback;
import com.buddiesmap.fbhandlers.FriendsInfoCallBack;
import com.buddiesmap.fbhandlers.LoggedUser;
import com.buddiesmap.fbhandlers.UserInfo;
import com.buddiesmap.fbhandlers.UserInfoCallBack;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static com.buddiesmap.MapUtils.getLatLongFromString;
import static com.buddiesmap.MapUtils.getMarkerOption;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String LOGGED_USER_REQUEST = "/me?fields=name,hometown,location";
    public static final String LOGGED_USER_FRIENDS = "com.buddiesmap.LOGGED_USER_FRIENDS";
    public static final int LOGGED_USER_INFO = 1;
    public static final int FRIEND_USER_INFO = 2;
    public static final int CHILD_THREAD_QUIT_LOOPER = 3;
    private static final String AUTH_TYPE = "rerequest";
    private final List<String> FB_PERMISSIONS = Arrays.asList("public_profile", "user_friends",
            "user_hometown", "user_location");
    private final LinkedList<Marker> mHometownMarkers = new LinkedList<>();
    private final LinkedList<Marker> mLocationMarkers = new LinkedList<>();
    BitmapDescriptor homeIcon;
    BitmapDescriptor locationIcon;
    RelativeLayout mProgressBarLayout;
    ProgressBar mProgressBar;
    TextView mProgressBarTextView;
    private LoggedUser mLoggedUser = LoggedUser.getInstance();
    private GoogleMap mMap;
    private boolean mHometownsVisible = true;
    private boolean mLocationsVisible = true;
    private LinearLayout mapButtons;
    private CallbackManager mCallbackManager;
    private BlockingQueue<UserInfo> mBlockingQ = new LinkedBlockingDeque<>();
    private LocalBroadcastManager bManager;
    private BroadcastReceiver bReceiver = new MainBroadcastReceiver();
    private Handler mainThreadHandler;
    private int mNumOfFriends = 0;
    private int mNumOfFriendsOnMap = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mProgressBarLayout = findViewById(R.id.progressBarLayout);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBarTextView = findViewById(R.id.progressBarTextView);
        mProgressBarLayout.setVisibility(View.INVISIBLE);

        mapButtons = findViewById(R.id.mapButtons);
        mapButtons.setVisibility(View.INVISIBLE);// these buttons are not needed before the login to FB

        initializeBroadcasts();

        initializeFBLogin();

        initializeMapButtons();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeBroadcasts() {
        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LOGGED_USER_FRIENDS);

        bManager.registerReceiver(bReceiver, intentFilter);
    }

    private void initializeFBLogin() {
        mainThreadHandler = new MyHandler(Looper.getMainLooper(), MainActivity.this);

        mCallbackManager = CallbackManager.Factory.create();
        final LoginButton mLoginButton = findViewById(R.id.fbLoginButton);

        // Set the initial permissions to request from the user while logging in
        mLoginButton.setPermissions(FB_PERMISSIONS);
        mLoginButton.setAuthType(AUTH_TYPE);

        // Register a callback to respond to the user
        mLoginButton.registerCallback(mCallbackManager, new FacebookLoginCallback(this));

        AccessTokenTracker accessTokenTracker = new FacebookLogoutCallback(this);
        accessTokenTracker.startTracking();

        if (AccessToken.getCurrentAccessToken() != null) {
            sendGraphRequests();
        }
    }

    private void initializeMapButtons() {
        homeIcon = BitmapDescriptorFactory.fromResource(R.drawable.home);
        locationIcon = BitmapDescriptorFactory.fromResource(R.drawable.cloc);

        // Button to control hometown markers
        final Button homeButton = findViewById(R.id.hometownButton);
        homeButton.setOnClickListener(v -> changeHometownsVisibility());

        // Button to control location markers
        final Button locationButton = findViewById(R.id.locButton);
        locationButton.setOnClickListener(v -> changeLocationsVisibility());
    }

    private void changeHometownsVisibility() {
        mHometownsVisible = !mHometownsVisible;
        for (Marker marker : mHometownMarkers) {
            marker.setVisible(mHometownsVisible);
        }
    }

    private void changeLocationsVisibility() {
        mLocationsVisible = !mLocationsVisible;
        for (Marker marker : mLocationMarkers) {
            marker.setVisible(mLocationsVisible);
        }
    }

    public void sendGraphRequests() {
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                LOGGED_USER_REQUEST,
                null, HttpMethod.GET,
                new UserInfoCallBack(this, mainThreadHandler)
        ).executeAsync();

        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null, HttpMethod.GET,
                new FriendsInfoCallBack(this)
        ).executeAsync();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void updateUserInfoOnMap() {
        String home = mLoggedUser.getUserInfo().getUserHometown();
        String location = mLoggedUser.getUserInfo().getUserLocation();
        Marker marker;

        if (location != null) {
            marker = setMarkerOnMap(location, false, "User's location");
            mLocationMarkers.add(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }
        if (home != null) {
            marker = setMarkerOnMap(home, true, "User's hometown");
            mHometownMarkers.add(marker);
            if (location == null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }
        }
    }

    private Marker setMarkerOnMap(String place, boolean isHome, String markerTitle) {
        LatLng latLong = getLatLongFromString(place, this);
        @NotNull
        MarkerOptions markerOpt = getMarkerOption(isHome ? homeIcon : locationIcon, latLong, markerTitle);
        return mMap.addMarker(markerOpt);
    }

    public void onUserLogout() {
        mMap.clear();
        mLoggedUser = null;
        mapButtons.setVisibility(View.INVISIBLE);
        mHometownMarkers.clear();
        mLocationMarkers.clear();
        mHometownsVisible = true;
        mLocationsVisible = true;
        mNumOfFriends = 0;
        mNumOfFriendsOnMap = 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bManager.unregisterReceiver(bReceiver);
    }

    private void updateProgressBar() {
        mProgressBar.setProgress(mNumOfFriendsOnMap, true/* * 100 / mNumOfFriends*/);
//        mProgressBar.setSecondaryProgress((mNumOfFriendsOnMap > 0 ? mNumOfFriendsOnMap :
//                mNumOfFriendsOnMap - 1) * 100 / mNumOfFriends);
        String progressTxt = "Loading " + mNumOfFriendsOnMap + "/" + mNumOfFriends;
        mProgressBarTextView.setText(progressTxt);

        if (mNumOfFriendsOnMap == mNumOfFriends) {
            //After completing the loading of all locations we delay the hiding of the progress bar
            //so the user will have a chance to see
            new Handler().postDelayed(() -> mProgressBarLayout.setVisibility(View.INVISIBLE)
                    , 1000);
        }
    }

    private class MyHandler extends Handler {
        WeakReference<MainActivity> mActivityReference;

        MyHandler(Looper looper, MainActivity activity) {
            super(looper);
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d("MAIN_THREAD", "Receive message from child thread.");
            final MainActivity activity = mActivityReference.get();
            UserInfo userInfo;

            if (activity != null) {
                if (msg.what == LOGGED_USER_INFO) {
                    userInfo = (UserInfo) msg.obj;
                    mLoggedUser.setUserInfo(userInfo);
                    updateUserInfoOnMap();
                    mapButtons.setVisibility(View.VISIBLE);
                } else if (msg.what == FRIEND_USER_INFO) {
                    userInfo = (UserInfo) msg.obj;
                    mLoggedUser.addFriendInfo(userInfo);
                    activity.mHometownMarkers.add(activity.setMarkerOnMap(userInfo.getUserHometown(), true, userInfo.getUserName()));
                    activity.mLocationMarkers.add(activity.setMarkerOnMap(userInfo.getUserLocation(), false, userInfo.getUserName()));


                    mNumOfFriendsOnMap++;
                    updateProgressBar();

                    Toast.makeText(activity, "Task one execute.", Toast.LENGTH_LONG).show();
                } else if (msg.what == CHILD_THREAD_QUIT_LOOPER) {
                    Toast.makeText(activity, "Quit child thread looper.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class MainBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null)
                return;

            switch (intent.getAction()) {
                case LOGGED_USER_FRIENDS:
                    ArrayList<String> userFriendIDs = intent.getStringArrayListExtra(LOGGED_USER_FRIENDS);
                    if (userFriendIDs == null) {
                        return;
                    }

                    mLoggedUser.setUserFriendIDs(userFriendIDs);
                    mNumOfFriends = userFriendIDs.size();

                    mProgressBarLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setMax(mNumOfFriends);
                    updateProgressBar();

                    for (String fbID : userFriendIDs) {
                        new GraphRequest(AccessToken.getCurrentAccessToken(),
                                "/" + fbID + "?fields=name,hometown,location",
                                null, HttpMethod.GET,
                                new UserInfoCallBack(MainActivity.this, mainThreadHandler)
                        ).executeAsync();
                    }

                    break;
            }
        }
    }
}
