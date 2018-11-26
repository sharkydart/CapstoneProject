package com.homebrewforlife.sharkydart.anyonecanfish.services;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LocationTasks {
    public static final String ACTION_GET_GPS_LOCATION = "get-gps-location";
    public static final String ACTION_FOUND_GPS_LOCATION = "found-gps-location";
    public static final String EXTRA_LATITUDE = "extra-latitude";
    public static final String EXTRA_LONGITUDE = "extra-longitude";
    public static final double DEFAULT_LAT = 39.065952;
    public static final double DEFAULT_LON = -84.479897;

    public static void getLastLocationTask(Context theContext, String theAction){
        if(ACTION_GET_GPS_LOCATION.equals(theAction)){
            //get the current gps location
            updateLocation(theContext);
        }
    }
    private static void updateLocation(final Context theContext){
        FusedLocationProviderClient mFusedLocationClient;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(theContext);
        if(ActivityCompat.checkSelfPermission(theContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object

                                location.getLatitude();
                                location.getLongitude();
                            }
                        }
                    });
        else
            Log.i("fart", "Somehow didn't get location permission...");
    }
}
