package com.example.carlbai.gpshacktx2;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;


public class MyActivityPhone extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {


    private LocationClient locationclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activity_phone);

        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(resp == ConnectionResult.SUCCESS){
            locationclient = new LocationClient(this,this,this);
            locationclient.connect();
        }
        else
        {
            Toast.makeText(this, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
        }

    }


    public void buttonClicked(View v) {
        if(v.getId() == R.id.btnLastLoc) {
            String temp = "";
            Log.v("Hello", "buttonClicked");
            if(locationclient!=null && locationclient.isConnected()) {
                Location loc =locationclient.getLastLocation();
                Log.v("Hello", "Last Known Location :" + loc.getLatitude() + "," + loc.getLongitude());
                temp = Double.toString(loc.getLatitude()) + ","  + Double.toString(loc.getLongitude() - .0005);
                //temp = "30.291217, -97.734133";
            }
            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=" + temp));
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_activity_phone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
