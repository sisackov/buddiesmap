package com.buddiesmap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LinearLayout mapButtons;

    private CallbackManager mCallbackManager;
    LoginButton mLoginButton;
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

        mapButtons = (LinearLayout) findViewById(R.id.mapButtons);
        mapButtons.setVisibility(View.INVISIBLE);// these buttons are not needed before the login to FB

        initializeFBLogin();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
//        mLoginButton.setPermissions(Arrays.asList("public_profile", "user_friends",
//                "user_hometown", "user_location"));
        mLoginButton.setAuthType(AUTH_TYPE);

        // Register a callback to respond to the user
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setResult(RESULT_OK);
                Log.d("Success", "Login");
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

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends",
                        "user_hometown", "user_location", "user_checkins "));
            }
        });
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
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
