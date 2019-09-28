package com.example.googlemapsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemapsapp.base.BaseActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int GPS_PERSMISSION_RQUEST_CODE = 1234;

    TextView userLocationText;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userLocationText = findViewById(R.id.user_location_text);

        mapView = findViewById(R.id.map_view);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        if (this.isGPSPermessionGranted()) {
            //Call your function

            getUserLocation();
        } else {
            askForGPSPermission();
        }
    }
    GoogleMap googleMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        drawUserMarkerOnMap();


    }
    Marker userMarker = null;
    public void drawUserMarkerOnMap(){
        if (myLocation == null){
            return;
        }
        if(googleMap == null) return;

         if(userMarker == null )
             userMarker = googleMap.addMarker( new MarkerOptions().position(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()))
                .title("I'm Here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
         else{
             userMarker.setPosition(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()));
         }
         googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(myLocation.getLatitude(),myLocation.getLongitude()),12f));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public boolean isGPSPermessionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
            // Permission is not granted
        }
        return true;
    }

    Location myLocation;
    MyLocationProvider myLocationProvider;

    public void getUserLocation() {
        if (myLocationProvider == null) {
            myLocationProvider = new MyLocationProvider(this);
            myLocationProvider.getUserLocation(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    myLocation = location;
                    drawUserMarkerOnMap();
                    userLocationText.setText(myLocation.getProvider() + " "
                            + myLocation.getLatitude() + " "
                            + myLocation.getLongitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.e("onProviderEnabled: ", provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.e("onProviderDisabled: ", provider);

                }
            });
        }
    }

    public void askForGPSPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            showMessage(R.string.gps_explaination, R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            GPS_PERSMISSION_RQUEST_CODE);

                }
            });
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GPS_PERSMISSION_RQUEST_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GPS_PERSMISSION_RQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    getUserLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(activity, "Sorry we can't access your location", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
