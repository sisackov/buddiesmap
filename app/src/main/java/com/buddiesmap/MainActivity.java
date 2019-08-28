package com.buddiesmap;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

//    final Button homeButton = null;
//    final Button locButton = null;
//    final Button chkButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        setContentView(R.layout.map_main);

//        final Button homeButton = (Button) findViewById(R.id.hometownButton);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
}
