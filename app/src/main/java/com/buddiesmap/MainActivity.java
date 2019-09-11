package com.buddiesmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static com.buddiesmap.MapUtils.getLatLongFromString;
import static com.buddiesmap.MapUtils.getMarkerOption;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String LOGGED_USER_INFO = "com.buddiesmap.LOGGED_USER_INFO";
    public static final String LOGGED_USER_FRIENDS = "com.buddiesmap.LOGGED_USER_FRIENDS";
    private static final String AUTH_TYPE = "rerequest";
    private final List<String> FB_PERMISSIONS = Arrays.asList("public_profile", "user_friends",
            "user_hometown", "user_location");
    private final LinkedList<Marker> mHometownMarkers = new LinkedList<>();
    private final LinkedList<Marker> mLocationMarkers = new LinkedList<>();
    BitmapDescriptor homeIcon;
    BitmapDescriptor locationIcon;
    private LoggedUser mLoggedUser = LoggedUser.getInstance();
    private GoogleMap mMap;
    private boolean mHometownsVisible = true;
    private boolean mLocationsVisible = true;

    private LinearLayout mapButtons;
    private CallbackManager mCallbackManager;

    private BlockingQueue<UserInfo> mBlockingQ = new LinkedBlockingDeque<>();

    private LocalBroadcastManager bManager;
    private BroadcastReceiver bReceiver = new MainBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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
        intentFilter.addAction(LOGGED_USER_INFO);
        intentFilter.addAction(LOGGED_USER_FRIENDS);

        bManager.registerReceiver(bReceiver, intentFilter);
    }

    private void initializeFBLogin() {
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

        // Button to control hometown overlay
        final Button homeButton = findViewById(R.id.hometownButton);
        homeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mHometownsVisible = !mHometownsVisible;
                        for (Marker marker : mHometownMarkers)
                            marker.setVisible(mHometownsVisible);
                    }
                }
        );

        // Button to control location overlay
        final Button locationButton = findViewById(R.id.locButton);
        locationButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLocationsVisible = !mLocationsVisible;
                        for (Marker marker : mLocationMarkers)
                            marker.setVisible(mLocationsVisible);
                    }
                }
        );
    }

    public void sendGraphRequests() {
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/me?fields=name,hometown,location",
                null, HttpMethod.GET,
                new UserInfoCallBack(this)
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

    private class MainBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null)
                return;

            switch (intent.getAction()) {
                case LOGGED_USER_INFO:
                    mLoggedUser.setUserInfo(intent.getParcelableExtra(LOGGED_USER_INFO));
                    updateUserInfoOnMap();
                    mapButtons.setVisibility(View.VISIBLE);
                    break;
                case LOGGED_USER_FRIENDS:
                    mLoggedUser.setUserFriends(intent.getStringArrayListExtra(LOGGED_USER_FRIENDS));
                    //updateUserInfoOnMap();
                    //todo


                    mapButtons.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
