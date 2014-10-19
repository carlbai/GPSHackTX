package com.example.carlbai.gpshacktx2;

/**
 * Created by carlbai on 10/18/14.
 */
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class ListenerService extends WearableListenerService implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private LocationClient locationclient;
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static final String MESSAGE = "Hello Wear!";

    private GoogleApiClient client;
    private String nodeId;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v("Hello", "onMessagedReceived");
        initApi();
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

            final String temp = Double.toString(loc.getLatitude()) + " "  + Double.toString(loc.getLongitude());


            if (nodeId != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                        Wearable.MessageApi.sendMessage(client, nodeId, temp, null);
                        client.disconnect();
                    }
                }).start();
            }
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
    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }
    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

}