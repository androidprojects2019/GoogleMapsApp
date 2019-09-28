package com.example.googlemapsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.List;

public class MyLocationProvider {
    Location location;
    LocationManager locationManager;
    boolean canAccessLocation;
    public static final long MIN_TIME_BETWEEN_UPDATES = 5000;
    public static final float MIN_Distance_BETWEEN_UPDATES = 10.0f;
    LocationListener locationListener;

    public MyLocationProvider(Context context) {
        locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        location = null;
        canAccessLocation = false;
    }

    @SuppressLint("MissingPermission")
    public Location getUserLocation(LocationListener locationListener) {
        boolean isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isNetworkEnabled && !isGPSEnabled) {
            location = null;
            canAccessLocation = false;
            return  getBestLastKnownLocation();
        }
        canAccessLocation = true;

        String provider = LocationManager.NETWORK_PROVIDER;

        if (isGPSEnabled) {
            provider = locationManager.GPS_PROVIDER;
        }

        locationManager.requestLocationUpdates(provider,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_Distance_BETWEEN_UPDATES, locationListener
        );
        location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            location = getBestLastKnownLocation();
        }

        return location;
    }

    public Location getBestLastKnownLocation() {
        List<String> providers = locationManager.getAllProviders();

//      for (int i = 0 ; i<providers.size();i++){
//          String provider = providers.get(i);
//      }

        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
            if (bestLocation == null) {
                bestLocation = l;
            } else if (bestLocation != null && l != null) {
                if (l.getAccuracy() > bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }
}
