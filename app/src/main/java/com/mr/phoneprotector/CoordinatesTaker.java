package com.mr.phoneprotector;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;

public class CoordinatesTaker {
    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            locationManager.removeUpdates(this);
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };
    private String coordinates;


    public CoordinatesTaker(Context context) {
        this.locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        this.coordinates = new String();
    }

    public String takeCoordinates () {
        String GPScoordinates = "GPS disabled";
        String NETWORKcoordinates = "NETWORK disabled";

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GPScoordinates = takeCoordinatesViaGPS();
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            NETWORKcoordinates = takeCoordinatesViaNETWORK();
        }



        locationManager.removeUpdates(locationListener);
        return GPScoordinates + "\n" + NETWORKcoordinates;
    }

    public void takeCoordinatesIn () {
        String GPScoordinates = "GPS disabled";
        String NETWORKcoordinates = "NETWORK disabled";

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GPScoordinates = takeCoordinatesViaGPS();
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            NETWORKcoordinates = takeCoordinatesViaNETWORK();
        }

        locationManager.removeUpdates(locationListener);
        this.coordinates = GPScoordinates + "\n" + NETWORKcoordinates;
    }

    private String takeCoordinatesViaGPS () {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListener);

            Thread.sleep(30000);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                String latitude = "Latitude: " + location.getLatitude();
                String longitude = "Longitude: " + location.getLongitude();
                return "GPS coordinates--->" + latitude + " " + longitude + ";";
            } else {
               return "Unable to get GPS coordinates";
            }
        } catch (SecurityException e) {
            return "GPS permissions disabled";
        } catch (InterruptedException e) {
            return "Amogus";
        }
    }

    private String takeCoordinatesViaNETWORK () {
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, 0, locationListener);

            Thread.sleep(10000);
            Location location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                String latitude = "Latitude: " + location.getLatitude();
                String longitude = "Longitude: " + location.getLongitude();
                return "NETWORK coordinates--->" + latitude + " " + longitude + ";";
            } else {
                return "Unable to get NetWork coordinates";
            }
        } catch (SecurityException e) {
            return "NetWork permissions disabled";
        } catch (InterruptedException e) {
            return "Amogus";
        }
    }

    public String getCoordinatesString() {
        return this.coordinates;
    }
}
