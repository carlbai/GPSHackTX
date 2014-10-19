package com.example.carlbai.gpshacktx2;

/**
 * Created by carlbai on 10/18/14.
 */
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

        if(messageEvent.getPath().equals("Location"))
        {
            Log.v("Hello", "Location");
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
        else if(messageEvent.getPath().equals("Navigation"))
        {
            Log.v("Hello", "Navigation");
            FileInputStream in = null;

            try {
                in = openFileInput("outputfile");
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line = bufferedReader.readLine();

                Log.v("Hello", line);

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + line + "&mode=w "));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
            wakeLock.acquire();

            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguardLock =  keyguardManager.newKeyguardLock("TAG");
            keyguardLock.disableKeyguard();

        }

    }

    private void showToast(String message) {
        Log.v("Hello", "buttonClicked");
        if(locationclient!=null && locationclient.isConnected()) {
            Location loc =locationclient.getLastLocation();

            Log.v("Hello", Double.toString(loc.getLatitude()));
            if(loc == null)
            {
                Log.v("Hello", "loc is null");
                locationclient = new LocationClient(this,this,this);
                locationclient.connect();
                loc = locationclient.getLastLocation();
            }
            Log.v("Hello", "Last Known Location :" + loc.getLatitude() + "," + loc.getLongitude());
            Toast.makeText(this, loc.getLatitude() + " " + loc.getLongitude(), Toast.LENGTH_SHORT).show();

            final String temp = Double.toString(loc.getLatitude()) + " "  + Double.toString(loc.getLongitude());
            final String coordinates = Double.toString(loc.getLatitude()) + ", "  + Double.toString(loc.getLongitude());


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

            String filename = "outputfile";
            String string = coordinates;
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(string.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
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