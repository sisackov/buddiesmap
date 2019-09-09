package com.buddiesmap;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.buddiesmap.fbhandlers.UserInfo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LinearLayout mapButtons;
    BitmapDescriptor homeIcon;
    BitmapDescriptor locationIcon;

    private CallbackManager mCallbackManager;
    LoginButton mLoginButton;
    UserInfo mLoggedUser;
    private static final String EMAIL = "email";
    private static final String USER_POSTS = "user_posts";
    private static final String AUTH_TYPE = "rerequest";

//    final Button homeButton = null;
//    final Button locButton = null;
//    final Button chkButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapButtons = findViewById(R.id.mapButtons);
        mapButtons.setVisibility(View.INVISIBLE);// these buttons are not needed before the login to FB

        initializeFBLogin();

        getUserInfo();

        initializeMapButtons();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeMapButtons() {
        homeIcon = BitmapDescriptorFactory.fromResource(R.drawable.home);
        locationIcon = BitmapDescriptorFactory.fromResource(R.drawable.cloc);

        //        final Button homeButton = (Button) findViewById(R.id.hometownButton);

        // Button to control hometown overlay
//        final Button homeButton = (Button) findViewById(R.id.hometownButton);
//        homButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v)
//            {
//                if(Globals.isRefreshed)
//                    Main.this.runOnUiThread(new Runnable() {
//                        public void run(){setHomeOverlay();}});
//            }
//        });

        // Button to control location overlay
//        final Button locButton = (Button) findViewById(R.id.locButton);
//        locButton.setOnClickListener(new OnClickListener(){
//            public void onClick(View v)
//            {
//                if(Globals.isRefreshed)
//                    Main.this.runOnUiThread(new Runnable() {
//                        public void run(){setLocationOverlay();}});
//            }
//        });

        // Button to control checkin overlay
//        final Button chkButton = (Button) findViewById(R.id.chckButton);
//        chkButton.setOnClickListener(new OnClickListener(){
//            public void onClick(View v)
//            {
//                if(!Globals.refreshInProgress)
//                    Main.this.runOnUiThread(new Runnable() {
//                        public void run(){setCheckinOverlay();}});
//            }
//        });

//        Globals.stateButton = (Button)Globals.mapActivity.findViewById(R.id.stateButton);
    }

    private void initializeFBLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton = findViewById(R.id.fbLoginButton);

        // Set the initial permissions to request from the user while logging in
        mLoginButton.setPermissions(Arrays.asList("public_profile", "user_friends",
                "user_hometown", "user_location"));
        mLoginButton.setAuthType(AUTH_TYPE);

        // Register a callback to respond to the user
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setResult(RESULT_OK);
                Log.d("Success", "Login");
                getFriendsList();
            }

            @Override
            public void onCancel() {
                setResult(RESULT_CANCELED);
                Toast.makeText(MainActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                // Handle exception
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

//        mLoginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends",
//                        "user_hometown", "user_location"/*, "user_checkins"*/));
//            }
//        });
    }

    private void getUserInfo() {
        List<String> places = new ArrayList<>(2);
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me?fields=name,hometown,location", null, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                /* handle the result */
                Log.e("User Info: ", response.toString());
                mLoggedUser = new UserInfo();
                JSONObject jsonObject;
                try {
                    JSONObject responseObject = response.getJSONObject();
                    if (responseObject.has("name")) {
                        mLoggedUser.setUserName(responseObject.getString("name"));
                    }
                    if (responseObject.has("hometown")) {
                        jsonObject = responseObject.getJSONObject("hometown");
                        mLoggedUser.setUserHometown(jsonObject.getString("name"));
                    }
                    if (responseObject.has("location")) {
                        jsonObject = responseObject.getJSONObject("location");
                        mLoggedUser.setUserLocation(jsonObject.getString("name"));
                    }

//                    JSONArray dataArray = responseObject.getJSONArray("name");
//                    for (int i = 0; i < dataArray.length(); i++) {
//                        JSONObject dataObject = dataArray.getJSONObject(i);
//                        String fbId = dataObject.getString("id");
//                        String fbName = dataObject.getString("name");
//                        Log.e("FbId", fbId);
//                        Log.e("FbName", fbName);
//                        places.add(fbId);
//                    }
//                    Log.e("fbfriendList", places.toString());
//                    List<String> list = places;
//                    String friends = "";
//                    if (list != null && list.size() > 0) {
//                        friends = list.toString();
//                        if (friends.contains("[")) {
//                            friends = (friends.substring(1, friends.length() - 1));
//                        }
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    updateMap();
                }
            }
        }).executeAsync();
    }

    private List<String> getFriendsList() {
        final List<String> friendslist = new ArrayList<>();
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", null, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                /* handle the result */
                Log.e("Friends List: 1", response.toString());
                try {
                    JSONObject responseObject = response.getJSONObject();
                    JSONArray dataArray = responseObject.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject = dataArray.getJSONObject(i);
                        String fbId = dataObject.getString("id");
                        String fbName = dataObject.getString("name");
                        Log.e("FbId", fbId);
                        Log.e("FbName", fbName);
                        friendslist.add(fbId);
                    }
                    Log.e("fbfriendList", friendslist.toString());
                    List<String> list = friendslist;
                    String friends = "";
                    if (list != null && list.size() > 0) {
                        friends = list.toString();
                        if (friends.contains("[")) {
                            friends = (friends.substring(1, friends.length() - 1));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    hideLoadingProgress();
                }
            }
        }).executeAsync();
        return friendslist;
    }

    private void hideLoadingProgress() {
        //TODO
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

    private void updateMap() {
        if (mLoggedUser != null) {
            String home = mLoggedUser.getUserHometown();
            String location = mLoggedUser.getUserLocation();
            LatLng latLong;
            MarkerOptions markerOpt;

            if (location != null) {
                latLong = getLatLongFromString(location);
                markerOpt = getMarkerOption(true, latLong, "User's location");
                mMap.addMarker(markerOpt);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
            }
            if (home != null) {
                latLong = getLatLongFromString(home);
                markerOpt = getMarkerOption(false, latLong, "User's hometown");
                mMap.addMarker(markerOpt);
                if (location == null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
                }
            }
        }
    }

    private MarkerOptions getMarkerOption(boolean isLocation, LatLng latLong, String markerTitle) {
        return new MarkerOptions()
                .position(latLong)
                .title(markerTitle)
                .icon(isLocation ? locationIcon : homeIcon);
    }

    private LatLng getLatLongFromString(String location) {
        LatLng res = null;
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(location, 1);
            if (addresses.size() > 0) {
                res = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
