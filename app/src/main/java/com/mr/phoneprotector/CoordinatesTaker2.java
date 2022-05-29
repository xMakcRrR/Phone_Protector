package com.mr.phoneprotector;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;

public class CoordinatesTaker2 {
    private LocationManager locationManager;
    private LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            GPScoordinates = "GPS Latitude: " + location.getLatitude()
                    + "; Longitude: " + location.getLongitude();
            locationManager.removeUpdates(this);
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };
    private LocationListener locationListenerNET = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            NETWORKcoordinates = "NETWORK Latitude: " + location.getLatitude()
                    + "; Longitude: " + location.getLongitude();
            locationManager.removeUpdates(this);
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };
    private String GPScoordinates;
    private String NETWORKcoordinates;


    public CoordinatesTaker2 (Context context) {
        this.locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        this.GPScoordinates = new String();
        this.NETWORKcoordinates = new String();
    }

    public void startTakeCoordinatesIn () {
        GPScoordinates = "GPS disabled";
        NETWORKcoordinates = "NETWORK disabled";

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            takeCoordinatesViaGPS();
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            takeCoordinatesViaNETWORK();
        }
    }

    private void takeCoordinatesViaGPS () {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListenerGPS);
        } catch (SecurityException e) { }
    }

    private void takeCoordinatesViaNETWORK () {
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, 0, locationListenerNET);
        } catch (SecurityException e) {}
    }

    public String getCoords () {
        return GPScoordinates + " " + NETWORKcoordinates;
    }
}