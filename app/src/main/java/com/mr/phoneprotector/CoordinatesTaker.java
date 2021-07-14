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


    public CoordinatesTaker(Context context) {
        this.locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

    }

    public String getCoordinates () {
        String GPScoordinates = "GPS disabled";
        String NETWORKcoordinates = "NETWORK disabled";

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GPScoordinates = getCoordinatesViaGPS();
            locationManager.removeUpdates(locationListener);
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            NETWORKcoordinates = getCoordinatesViaNETWORK();
            locationManager.removeUpdates(locationListener);
        }

        locationManager.removeUpdates(locationListener);
        return GPScoordinates + "\n" + NETWORKcoordinates;
    }

    private String getCoordinatesViaGPS () {
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

    private String getCoordinatesViaNETWORK () {
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
}
