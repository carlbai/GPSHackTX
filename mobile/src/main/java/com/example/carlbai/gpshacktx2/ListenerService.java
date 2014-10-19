package com.example.carlbai.gpshacktx2;

/**
 * Created by carlbai on 10/18/14.
 */
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;


public class ListenerService extends WearableListenerService implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private LocationClient locationclient;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v("Hello", "onMessagedReceived");
        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(resp == ConnectionResult.SUCCESS){
            locationclient = new LocationClient(this,this,this);
            locationclient.connect();
        }
        else
        {
            Toast.makeText(this, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
        }


        showToast(messageEvent.getPath());
    }

    private void showToast(String message) {
        Log.v("Hello", "buttonClicked");
        if(locationclient!=null && locationclient.isConnected()) {
            Location loc =locationclient.getLastLocation();
            Log.v("Hello", "Last Known Location :" + loc.getLatitude() + "," + loc.getLongitude());
            Toast.makeText(this, loc.getLatitude() + " " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}