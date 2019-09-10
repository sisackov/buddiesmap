package com.buddiesmap;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

class MapUtils {
    @NotNull
    static MarkerOptions getMarkerOption(BitmapDescriptor icon, LatLng latLong, String markerTitle) {
        return new MarkerOptions()
                .position(latLong)
                .title(markerTitle)
                .icon(icon);
    }

    static LatLng getLatLongFromString(String location, Context context) {
        LatLng res = null;
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
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
}
