package com.babbangona.accesscontrol;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Master {

    public static String time = "";
    public static String longitude = "";
    public static String latitude = "";


    // This is the declaration of the version number


    public static String initialiseLocationListener(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new android.location.LocationListener() {

            public void onLocationChanged(Location location) {

                time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(location.getTime());
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());

                if (!longitude.trim().equalsIgnoreCase("null") || longitude != null) {
                   /* SessionManagement sessionManagement = new SessionManagement(context);
                    sessionManagement.createGPSSession(latitude,longitude);*/
                }

                if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                    //Toast.makeText(context, "GPS Longitude" + location.getLongitude() +" Latitude"+location.getLatitude(), Toast.LENGTH_LONG).show();
                    Log.d("Location", "Time GPS: " + time);
                }// This is what we want!
                else {

                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

//        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);

        if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            try {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } catch (Exception e) {

            }
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
            // Note: To Stop listening use: locationManager.removeUpdates(locationListener)
        } catch (Exception e) {
            e.printStackTrace();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            time = dateFormat.format(date);
        }
        if (time.matches("")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            time = dateFormat.format(date);
        }
        return time + "//" + longitude + "//" + latitude;
    }

    public static void checkPermission(Context context) {

        try {
            if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            }
        } catch (Exception e) {
        }


    }

    public void enableLocations(final Context mCtx) {
        LocationManager lm = (LocationManager) mCtx.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }


        if (!gps_enabled && !network_enabled) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.dismiss();
                            mCtx.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

        }
    }


}
