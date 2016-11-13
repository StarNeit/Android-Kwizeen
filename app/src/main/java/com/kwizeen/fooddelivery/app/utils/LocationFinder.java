package com.kwizeen.fooddelivery.app.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.models.Seller;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LocationFinder extends Service implements LocationListener {

    private Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    @Override
    public void onCreate(){
        super.onCreate();
        mContext = this;
        getLocation();

    }
    public LocationFinder(){
    }

    public LocationFinder(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    @SuppressLint("NewApi")
    public void stopUsingGPS() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(LocationFinder.this);
    }
  
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
      
        // return latitude
        return latitude;
    }
  
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
      
        // return longitude
        return longitude;
    }
  
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
  
    @Override
    public void onLocationChanged(Location location) {
        Log.d("FriendPos", String.valueOf(location.getLatitude()) + "-" + String.valueOf(location.getLongitude()));

        Global.g_Lat = location.getLatitude();
        Global.g_Lon = location.getLongitude();

        Intent intent = new Intent();
        intent.setAction(Global.BROADCASTRECEIVER_FILTER_LOCATIONCHANGE);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        //Send my location to server when my location changed
        if(Global.g_Seller != null) {
            if (Global.g_Seller.getSeller_id() > 0) {
                new CCHttpHandler() {
                    @Override
                    public HttpURLConnection getHttpRequestMethod() {
                        String url = Global.SELLER_UPDATE_LOCATION_URL;
                        String urlParameters = "id=" + Global.g_Seller.getSeller_id() + "&lat=" + Global.g_Lat + "&lon=" + Global.g_Lon;
                        return CCHttpFunc.PostHttpRequestMethod(url, urlParameters);
                    }

                    @Override
                    public void onResponse(String result) {
                    }
                }.execute();
            }
        }
        if(Global.g_Buyer != null) {
            if (Global.g_Buyer.getBuyer_id() > 0) {
                new CCHttpHandler() {
                    @Override
                    public HttpURLConnection getHttpRequestMethod() {
                        String url = Global.BUYER_UPDATE_LOCATION_URL;
                        String urlParameters = "id=" + Global.g_Buyer.getBuyer_id() + "&lat=" + Global.g_Lat + "&lon=" + Global.g_Lon;
                        CommonFunc.AppLog(urlParameters);
                        return CCHttpFunc.PostHttpRequestMethod(url, urlParameters);
                    }
                    @Override
                    public void onResponse(String result) {
                    }
                }.execute();
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}