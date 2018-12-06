package com.example.rohithkumar.cameramapsapplication;

import android.Manifest;
import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Console;
import java.util.List;

public class MyMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public Geocoder geocoder;
    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;
    LatLng currentCoordinates = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_maps);

        LocationManager currentLocation = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener currentLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


try {
    currentLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
            currentLocationListener);
    latitude = currentLocation.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            .getLatitude();
    longitude = currentLocation.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            .getLongitude();
    currentCoordinates = new LatLng(latitude, longitude);
}
catch (SecurityException e)
{

}


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        public void onMapReady (GoogleMap googleMap){
            // Add a marker in Sydney, Australia,
            // and move the map's camera to the same location.
            //LatLng sydney = new LatLng(-33.852, 151.211);
        //LatLng sydney = currentCoordinates;
            googleMap.addMarker(new MarkerOptions().position(currentCoordinates)
                    .title("Marker in Sydney").icon(BitmapDescriptorFactory.fromResource(R.mipmap.me)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinates,15));
        }

//    //Getting current location
//    private void getCurrentLocation() {
//        mMap.clear();
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//        if (location != null) {
//            //Getting longitude and latitude
//            longitude = location.getLongitude();
//            latitude = location.getLatitude();
//
//            //moving the map to location
//            moveMap();
//        }
//    }
//    private void moveMap() {
//        /**
//         * Creating the latlng object to store lat, long coordinates
//         * adding marker to map
//         * move the camera with animation
//         */
//        LatLng latLng = new LatLng(latitude, longitude);
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .draggable(true)
//                .title("Marker in India"));
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//
//
//    }

}
